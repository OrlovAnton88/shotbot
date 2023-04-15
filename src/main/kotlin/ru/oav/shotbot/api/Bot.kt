package ru.oav.shotbot.api

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import ru.oav.shotbot.config.BotProperty
import ru.oav.shotbot.config.GameWebSocketHandler
import ru.oav.shotbot.model.Game
import ru.oav.shotbot.model.GameEvent

@Component
class Bot(
    val botProperty: BotProperty,
    val telegramBotsApi: TelegramBotsApi,
    val gameWebSocketHandler: GameWebSocketHandler,
) : TelegramLongPollingBot() {

    @PostConstruct
    fun postConstruct() {
        telegramBotsApi.registerBot(this)
    }

    override fun getBotToken(): String = botProperty.token

    override fun getBotUsername(): String = botProperty.username

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()) {
            val message = update.message
            val chatId = message.chatId

            val responseText = if (message.hasText()) {
                val messageText = message.text
                if (isTaskAnswer(message) && !isBotCommand(messageText)) {
                    val currentTask = Game.currentTask
                    if (currentTask == null) {
                        sendNotification(chatId, "Раунд еще не начался")
                        return
                    }
                    if (currentTask.answered) {
                        sendNotification(chatId, "Правильно, но тебя опередили!")
                        return
                    } else if (currentTask.answer.toString() == message.text.trim()) {
                        val isRed = Game.currentPair.first().userId == message.from.id
                        var icon = RED_CIRCLE_TG
                        if(!isRed){
                            icon = GREEN_APPLE_TG
                        }
                        currentTask.answered = true
                        sendNotification(chatId, "Правильно")
                        Game.players.forEach {
                            sendNotification(it.chatId, " Выйграл(а) ${message.from.firstName} $icon")
                        }
                        return
                    } else {
                        sendNotification(chatId, "А вот и нет")
                        return
                    }
                }

                when (messageText) {
//                    BotCommands.START.value -> "Вилли этому господину!"
                    BotCommands.START.value -> {
                        gameWebSocketHandler.send(GameEvent.PREPARE)
                        "Привет, ${message.from.firstName}!"
                    }
                    BotCommands.JOIN.value -> joinGame(message)
                    BotCommands.CREATE.value -> createGame(message)
                    BotCommands.DROP.value -> finishGame(message)
                    BotCommands.PREPARE_NEXT_ROUND.value -> prepareNextRound(message)
                    BotCommands.START_NEXT_ROUND.value -> startNextRound(message)
                    BotCommands.FILL_LEFT.value -> {
                        gameWebSocketHandler.send(GameEvent.FILL_LEFT)
                        "Наливаем слева"
                    }

                    BotCommands.FILL_RIGHT.value -> {
                        gameWebSocketHandler.send(GameEvent.FILL_RIGHT)
                        "Наливаем справа"
                    }

                    else -> "Неизвестная команда: *$messageText*"
                }
            } else {
                "Я понимаю только текст"
            }

            sendNotification(chatId, responseText)
        }
    }

    private fun isTaskAnswer(message: Message): Boolean {
        if (!Game.isStarted()) return false
        if (Game.currentPair.none { it.chatId == message.chatId }) return false
        if (Game.currentTask == null) return false
        return true
    }

    private fun sendNotification(chatId: Long, responseText: String) {
        val responseMessage = SendMessage(chatId.toString(), responseText)
        responseMessage.enableMarkdown(true)
        execute(responseMessage)
    }

    fun joinGame(message: Message): String {
        if (!Game.isStarted()) return "Игра еще не начата :-р"
        val user = message.from
        Game.join(message.chatId, user.id, user.firstName)
        return "Вы присоединились"
    }

    fun createGame(message: Message): String {
        if (Game.isStarted()) return "Игра уже начата :-р"
        if (!isMasterMessage(message)) return "Неа..."
        Game.start(message.chatId, message.from.id, message.from.firstName)
        return "Игра создана"
    }

    fun finishGame(message: Message): String {
        if (!isMasterMessage(message)) return "Неа..."
        Game.finish()
        return "Игра завершена"
    }

    fun prepareNextRound(message: Message): String {
        if (!isMasterMessage(message)) return "Неа..."
        if (!Game.isStarted()) return "Игра еще не началась"
        Game.createNextPairAndTask()
        val nextPair = Game.currentPair



        Game.players
            .filter { it.chatId != message.chatId }
            .forEach {
                sendNotification(
                    it.chatId, """
                Следующими играют: ${nextPair.first().name} $RED_CIRCLE_TG & ${nextPair.last().name} $GREEN_APPLE_TG
            """.trimIndent()
                )
            }

        return """
                Следующими играют: ${nextPair.first().name} & ${nextPair.last().name}
            """.trimIndent()
    }

    fun startNextRound(message: Message): String {
        if (!isMasterMessage(message)) return "Неа..."
        if (Game.currentTask == null) return "Неа..."
        val task = Game.currentTask!!.task
        Game.currentPair.forEach {
            sendNotification(it.chatId, task)
        }
        return "Отправлено  ${Game.currentPair.first().name} & ${Game.currentPair.last().name}"
    }


    private fun isMasterMessage(message: Message): Boolean {
        return botProperty.masterId == message.from.id
    }

    companion object{
        private const val RED_CIRCLE_TG = "\uD83D\uDD34"
        private const val GREEN_APPLE_TG = "\uD83C\uDF4F"
    }
}
