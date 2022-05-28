package com.bignerdranch.nyethack

lateinit var player: Player

fun main() {
    narrate("Welcome to NyetHack!")
    narrate("---")
    val playerName = promptHeroName()
    player = Player(playerName)
    // changeNarratorMood()
    narrate("---")

    Game.play()
}

private fun promptHeroName(): String {
    narrate("A hero enters the town of Kronstadt. What is their name?")

    narrate("  Madrigal", ::makeYellow)
    return "Madrigal"
}

fun makeYellow(message: String) = "\u001b[33;1m$message\u001b[0m"
