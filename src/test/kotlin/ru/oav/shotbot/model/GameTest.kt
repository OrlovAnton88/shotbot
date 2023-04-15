package ru.oav.shotbot.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class GameTest {

    @Test
    fun createNextPairAndTask() {

        Game.start(1L, 10L, "Anton");
        Game.join(2L, 11L, "Alex")
        Game.join(3L, 12L, "Stas")
        Game.join(4L, 13L, "Dima")
        Game.createNextPairAndTask()

        val firstMoveUsers = Game.currentPair.map { it.userId }

        Game.createNextPairAndTask()

        Assertions.assertTrue(Game.currentPair.map { it.userId }.filter { firstMoveUsers.contains(it) }.isEmpty())

    }
}
