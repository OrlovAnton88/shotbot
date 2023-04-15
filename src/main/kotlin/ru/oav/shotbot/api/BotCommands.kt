package ru.oav.shotbot.api

enum class BotCommands(val value: String) {
    START("/start"),
    JOIN("/join"),
    CREATE("/create"),
    DROP("/drop"),
    PREPARE_NEXT_ROUND("/prepareNextRound"),
    START_NEXT_ROUND("/startNextRound"),
    FILL_LEFT("/fill_left"),
    FILL_RIGHT("/fill_right")


}

fun isBotCommand(value: String): Boolean {
    return BotCommands.values().any { it.value == value }
}
