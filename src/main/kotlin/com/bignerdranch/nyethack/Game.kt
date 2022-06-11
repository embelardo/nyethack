package com.bignerdranch.nyethack

import java.lang.NumberFormatException
import kotlin.system.exitProcess

object Game {

    private val worldMap = listOf(
        listOf(
            TownSquare(),
            Tavern(),
            Room("Back Room"),
            MonsterRoom("Cold Room", Draugr()),
            MonsterRoom("Workshop")
        ),
        listOf(
            MonsterRoom("Long Corridor", Werewolf()),
            Room("Generic Room"),
            MonsterRoom("Storage Room", Dragon()),
            MonsterRoom("Smithy")
        ),
        listOf(
            MonsterRoom("Dungeon", Draugr()),
            MonsterRoom("Cave", Dragon()),
            MonsterRoom("Pit", Werewolf())
        )
    )

    private var currentRoom: Room = worldMap[0][0]
    private var currentPosition = Coordinate(0, 0)
    private var exit = false

    init {
        narrate("Welcome, adventurer")
        val mortality = if (player.isImmortal) "an immortal" else "a mortal"
        narrate("${player.name}, $mortality, has ${player.healthPoints} health points")
        println("---")
    }

    fun play() {
        while (!exit) {
            narrate("${player.name} of ${player.hometown}, ${player.title}, is in ${currentRoom.description()}")
            currentRoom.enterRoom()

            print("> Enter your command: ")
            GameInput(readLine()).processCommand()
        }
        exitProcess(0)
    }

    fun cast(spell: String) {
        if ("fireball".equals(spell, ignoreCase = true)) {
            player.castFireball()
        } else {
            narrate("I don't know what spell that is")
        }
    }

    fun fight() {
        val monsterRoom = currentRoom as? MonsterRoom
        val currentMonster = monsterRoom?.monster
        if (currentMonster == null) {
            narrate("There's nothing to fight here")
            return
        }

        var combatRound = 0
        val previousNarrationModifier = narrationModifier
        narrationModifier = { it.addEnthusiasm(enthusiasmLevel = combatRound) }

        while (player.healthPoints > 0 && currentMonster.healthPoints > 0) {
            combatRound++

            player.attack(currentMonster)
            if (currentMonster.healthPoints > 0) {
                currentMonster.attack(player)
            }
            Thread.sleep(1000)
        }
        narrationModifier = previousNarrationModifier

        if (player.healthPoints <= 0) {
            narrate("You have been defeated! Thanks for playing")
            exitProcess(0)
        } else {
            narrate("${currentMonster.name} has been defeated")
            monsterRoom.monster = null
        }
    }

    fun move(direction: Direction) {
        val newPosition = currentPosition move direction
        val newRoom = worldMap[newPosition].orEmptyRoom()

        narrate("The hero moves ${direction.name}")
        currentPosition = newPosition
        currentRoom = newRoom
    }

    private operator fun List<List<Room>>.get(coordinate: Coordinate) =
        getOrNull(coordinate.y)?.getOrNull(coordinate.x)

    fun map() {
        worldMap.forEach { rooms ->
            println()
            rooms.forEachIndexed { index, room ->
                if (currentRoom == room) {
                    print(makeYellow("[${room.name}]"))
                } else {
                    print("[${room.name}]")
                }
                if (index < rooms.size - 1) {
                    print("  ")
                }
            }
        }
        println("\n")
    }

    fun sellLoot() {
        when (val currentRoom = currentRoom) {
            is TownSquare -> {
                player.inventory.forEach { item ->
                    if (item is Sellable) {
                        val sellPrice = currentRoom.sellLoot(item)
                        narrate("Sold ${item.name} for $sellPrice gold")
                        player.gold += sellPrice
                    } else {
                        narrate("Your ${item.name} can't be sold")
                    }
                }
                player.inventory.removeAll { it is Sellable }
            }
            else -> narrate("You cannot sell anything here")
        }
    }

    fun takeLoot() {
        val loot = currentRoom.lootBox.takeLoot()
        if (loot == null) {
            narrate("${player.name} approaches the loot box, but it is empty")
        } else {
            narrate("${player.name} now has a ${loot.name}")
            player.inventory += loot
        }
    }

    private class GameInput(arg: String?) {
        private val input = arg ?: ""
        val command = input.split(" ")[0]
        val argument = input.split(" ").getOrElse(1) { "" }

        fun processCommand() = when (command.lowercase()) {
            "cast" -> cast(argument)
            "exit" -> exit = true
            "fight" -> fight()
            "map" -> map()
            "move" -> {
                val direction = Direction.values()
                    .firstOrNull { it.name.equals(argument, ignoreCase = true) }
                if (direction != null) {
                    move(direction)
                } else {
                    narrate("I don't know what direction that is")
                }
            }
            "prophesize" -> player.prophesize()
            "quit" -> exit = true
            "ring" -> {
                try {
                    if (currentRoom is TownSquare) {
                        (currentRoom as TownSquare).ringBell(argument.toInt())
                    } else {
                        narrate("Sorry, '${currentRoom.description()}' doesn't have a bell to ring!")
                    }
                } catch (e: NumberFormatException) {
                    narrate("I don't know how many times you want to ring the bell")
                }
            }
            "sell" -> {
                if (argument.equals("loot", ignoreCase = true)) {
                    sellLoot()
                } else {
                    narrate("I don't know what you're trying to sell")
                }
            }
            "take" -> {
                if (argument.equals("loot", ignoreCase = true)) {
                    takeLoot()
                } else {
                    narrate("I don't know what you're trying to take")
                }
            }
            else -> narrate("I'm not sure what you're trying to do")
        }
    }

}
