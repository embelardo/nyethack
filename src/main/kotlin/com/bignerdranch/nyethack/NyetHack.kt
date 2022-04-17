package com.bignerdranch.nyethack

val player = Player()

fun main() {
    narrate("${player.name} is ${player.title}")
    player.changeName("Aurelia")
    // com.bignerdranch.nyethack.changeNarratorMood()
    narrate("${player.name}, ${player.title}, heads to the town square")
    visitTavern()
    narrate("---")
    player.castFireball()
}

private fun promptHeroName(): String {
    narrate(
        "A hero enters the town of Kronstadt. What is their name?",
        ::makeYellow
    )

    println("  Madrigal")
    return "Madrigal"
}

private fun makeYellow(message: String) = "\u001b[33;1m$message\u001b[0m"
