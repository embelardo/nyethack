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

fun visitTavern() {
    narrate("${player.name} enters $TAVERN_NAME")
    narrate("---")
    narrate("There are several items for sale:")
    narrate("  *** Welcome to Taernyl's Folly ***")
    narrate(formatMenuItems())
    narrate("---")

    val patrons: MutableSet<String> = firstNames.shuffled()
        .zip(lastNames.shuffled()) { firstName, lastName -> "$firstName $lastName" }
        .toMutableSet()

    val patronGold = mutableMapOf(
        TAVERN_MASTER to 86.00,
        player.name to 4.50,
        *patrons.map { it to 6.00 }.toTypedArray()
    )

    narrate("${player.name} sees several patrons in the tavern:")
    narrate("  ${patrons.joinToString()}")

    narrate("---")
    val itemOfDay = patrons.flatMap { getFavoriteMenuItems(it) }.random()
    narrate("The item of the day is the $itemOfDay")

    narrate("---")
    repeat(3) {
        placeOrder(patrons.random(), menuItems.random(), patronGold)
    }
    narrate("---")
    displayPatronBalances(patronGold)

    narrate("---")
    patrons
        .filter { patron -> patronGold.getOrDefault(patron, 0.0) < 4.0 }
        .also { departingPatrons ->
            patrons -= departingPatrons
            patronGold -= departingPatrons
        }
        .forEach { patron ->
            narrate("${player.name} sees $patron departing the tavern")
        }
    narrate("There are still some patrons in the tavern:")
    narrate("  ${patrons.joinToString()}")
}

private fun placeOrder(
    patronName: String,
    menuItemName: String,
    patronGold: MutableMap<String, Double>
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

private fun getFavoriteMenuItems(patron: String): List<String> {
    return when (patron) {
        "Alex Ironfoot" -> menuItems.filter { menuItem ->
            menuItemTypes[menuItem]?.contains("dessert") == true
        }
        else -> menuItems.shuffled().take(Random.nextInt(1..2))
    }
}

private fun displayPatronBalances(patronGold: Map<String, Double>) {
    narrate("${player.name} intuitively knows how much money each patron has:")
    patronGold.forEach { (patron, balance) ->
        narrate("  $patron has ${"%.2f".format(balance)} gold")
    }
}
