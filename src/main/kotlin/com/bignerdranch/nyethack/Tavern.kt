package com.bignerdranch.nyethack

import java.io.File
import kotlin.random.Random
import kotlin.random.nextInt

private const val TAVERN_MASTER = "Taernyl"
private const val TAVERN_NAME = "$TAVERN_MASTER's Folly"

private val firstNames = setOf("Alex", "Mordoc", "Sophie", "Tariq")
private val lastNames = setOf("Ironfoot", "Fernsworth", "Baggins", "Downstrider")

private val menuData = File("data/tavern-menu-data.txt")
    .readText()
    .split("\n")
    .map { it.split(",") }

private val menuItems = menuData.map { (_, name, _) -> name }

private val menuItemTypes = menuData.associate { (type, name, _) ->
    name to type
}

private val menuItemPrices = menuData.associate { (_, name, price) ->
    name to price.toDouble()
}

class Tavern : Room(TAVERN_NAME) {

    private val patrons: MutableSet<String> = firstNames.shuffled()
        .zip(lastNames.shuffled()) { firstName, lastName -> "$firstName $lastName" }
        .toMutableSet()

    private val patronGold = mutableMapOf(
        TAVERN_MASTER to 86.00,
        player.name to 4.50,
        *patrons.map { it to 6.00 }.toTypedArray()
    )

    val itemOfDay = patrons.flatMap { getFavoriteMenuItems(it) }.random()

    override val status = "Busy"

    override val lootBox: LootBox<Key> =
        LootBox(Key("key to Nogartse's evil lair"))

    override fun enterRoom() {
        narrate("${player.name} enters $TAVERN_NAME")
        println("---")

        narrate("There are several items for sale:")
        narrate("  *** Welcome to Taernyl's Folly ***")
        narrate(formatMenuItems())
        println("---")

        narrate("The item of the day is the $itemOfDay")
        println("---")

        narrate("${player.name} sees several patrons in the tavern:")
        narrate("  ${patrons.joinToString()}")
        println("---")

        placeOrder(patrons.random(), menuItems.random())
    }

    private fun placeOrder(
        patronName: String,
        menuItemName: String
    ) {
        val itemPrice = menuItemPrices.getValue(menuItemName)
        narrate("$patronName speaks with $TAVERN_MASTER to place an order")
        if (itemPrice <= patronGold.getOrDefault(patronName, 0.0)) {
            val action = when (menuItemTypes[menuItemName]) {
                "shandy", "elixir" -> "pours"
                "meal" -> "serves"
                else -> "hands"
            }
            narrate("  $TAVERN_MASTER $action $patronName a $menuItemName")
            narrate("  $patronName pays $TAVERN_MASTER $itemPrice gold")
            patronGold[patronName] = patronGold.getValue(patronName) - itemPrice
            patronGold[TAVERN_MASTER] = patronGold.getValue(TAVERN_MASTER) + itemPrice
        } else {
            narrate("  $TAVERN_MASTER says, \"You need more coin for a $menuItemName\"")
        }
    }

}

fun formatMenuItems(): String {
    val builder = StringBuilder()
    menuItems.toList().sorted().forEachIndexed { index, name ->
        val type = menuItemTypes[name]
        val price = menuItemPrices[name]
        val itemNumber = index + 1
        builder.append("  %d  %-25s : %16s  %5s".format((itemNumber), name, type, price))
        if (itemNumber < menuItems.size) builder.append("\n")
    }
    return builder.toString()
}

private fun getFavoriteMenuItems(patron: String): List<String> {
    return when (patron) {
        "Alex Ironfoot" -> menuItems.filter { menuItem ->
            menuItemTypes[menuItem]?.contains("dessert") == true
        }
        else -> menuItems.shuffled().take(Random.nextInt(1..2))
    }
}
