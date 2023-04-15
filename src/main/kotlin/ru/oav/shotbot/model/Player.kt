package ru.oav.shotbot.model

data class Player(
    val chatId: Long,
    val userId: Long,
    val name: String,
    var moves: Int = 0
)
