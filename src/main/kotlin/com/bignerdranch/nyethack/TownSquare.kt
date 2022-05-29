package com.bignerdranch.nyethack

open class TownSquare : Room("The Town Square") {

    override val status = "Bustling"

    private var bellSound = "GWONG"

    private val hatDropOffBox = DropOffBox<Hat>()

    private val gemDropOffBox = DropOffBox<Gemstones>()

    final override fun enterRoom() {
        narrate("The villagers rally and cheer as the hero enters")
        ringBell()
    }

    fun ringBell(count: Int = 1) {
        count.downTo(1).forEach { _ ->
            narrate("The bell tower announces the hero's presence: $bellSound")
        }
    }

    fun <T> sellLoot(
        loot: T
    ): Int where T : Loot, T : Sellable {
        return when (loot) {
            is Hat -> hatDropOffBox.sellLoot(loot)
            is Gemstones -> gemDropOffBox.sellLoot(loot)
            else -> 0
        }
    }

}
