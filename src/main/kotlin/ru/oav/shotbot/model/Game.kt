package ru.oav.shotbot.model

import ru.oav.shotbot.exception.GameAlreadyStartedException
import ru.oav.shotbot.exception.GameNotStartedException
import java.time.Instant

object Game {
    val players: MutableList<Player> = mutableListOf()
    var creator: Long? = null
    var startedAt: Instant? = null


    fun start(chatId: Long, userId: Long, name: String) {
        if (startedAt != null) throw GameAlreadyStartedException()

        creator = userId
        players += Player(
            chatId = chatId,
            userId = userId,
            name = name
        )
        startedAt = Instant.now()
    }

    fun join(chatId: Long, userId: Long, name: String) {
        if (startedAt == null) throw GameNotStartedException()
        players += Player(
            chatId = chatId,
            userId = userId,
            name = name
        )
    }

    fun finish() {
        players.clear()
        creator = null
        startedAt = null
    }
}
