package com.bignerdranch.nyethack

lateinit var player: Player

fun main() {
    narrate("Welcome to NyetHack!")
    println("---")
    val playerName = promptHeroName()
    player = Player(playerName)
    println("---")
    changeNarratorMood()
    println("---")

    Game.play()
}

private fun promptHeroName(): String {
    narrate("A hero enters the town of Kronstadt. What is their name?")

    val input = readLine()
    require(input != null && input.isNotEmpty()) {
        "The hero must have a name."
    }

    return input
}

fun makeYellow(message: String) = "\u001b[33;1m$message\u001b[0m"
