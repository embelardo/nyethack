package com.bignerdranch.nyethack

import java.lang.NumberFormatException

object Game {

    private val worldMap = listOf(
        listOf(TownSquare(), Tavern(), Room("Back Room")),
        listOf(Room("A Long Corridor"), Room("A Generic Room")),
        listOf(Room("The Dungeon"))
    )

    private var currentRoom: Room = worldMap[0][0]
    private var currentPosition = Coordinate(0, 0)
    private var exit = false

    init {
        narrate("Welcome, adventurer")
        val mortality = if (player.isImmortal) "an immortal" else "a mortal"
        narrate("${player.name}, $mortality, has ${player.healthPoints} health points")
        narrate("---")
    }

    fun play() {
        while (!exit) {
            narrate("${player.name} of ${player.hometown}, ${player.title}, is in ${currentRoom.description()}")
            currentRoom.enterRoom()

            print("> Enter your command: ")
            GameInput(readLine()).processCommand()
        }
    }

    private class GameInput(arg: String?) {
        private val input = arg ?: ""
        val command = input.split(" ")[0]
        val argument = input.split(" ").getOrElse(1) { "" }

        fun processCommand() = when (command.lowercase()) {
            "cast" -> {
                cast(argument)
            }
            "exit" -> {
                exit = true
            }
            "map" -> {
                map()
            }
            "move" -> {
                val direction = Direction.values()
                    .firstOrNull { it.name.equals(argument, ignoreCase = true) }
                if (direction != null) {
                    move(direction)
                } else {
                    narrate("I don't know what direction that is")
                }
            }
            "prophesize" -> {
                player.prophesize()
            }
            "quit" -> {
                exit = true
            }
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
            else -> narrate("I'm not sure what you're trying to do")
        }
    }

    fun cast(spell: String) {
        if ("fireball".equals(spell, ignoreCase = true)) {
            player.castFireball()
        } else {
            narrate("I don't know what spell that is")
        }
    }

    fun move(direction: Direction) {
        val newPosition = direction.updateCoordinate(currentPosition)
        val newRoom = worldMap.getOrNull(newPosition.y)?.getOrNull(newPosition.x)

        if (newRoom != null) {
            narrate("The hero moves ${direction.name}")
            currentPosition = newPosition
            currentRoom = newRoom
        } else {
            narrate("You cannot move ${direction.name}")
        }
    }

    fun map() {
        worldMap.forEach { rooms ->
            println()
            rooms.forEach { room ->
                if (currentRoom == room) {
                    print(makeYellow("X "))
                } else {
                    print("O ")
                }
            }
        }
        println("\n")
    }

}
