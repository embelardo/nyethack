package com.bignerdranch.nyethack

open class TownSquare : Room("The Town Square") {

    override val status = "Bustling"

    private var bellSound = "GWONG"

    final override fun enterRoom() {
        narrate("The villagers rally and cheer as the hero enters")
        ringBell()
    }

    fun ringBell(count: Int = 1) {
        count.downTo(1).forEach { _ ->
            narrate("The bell tower announces the hero's presence: $bellSound")
        }
    }

}
