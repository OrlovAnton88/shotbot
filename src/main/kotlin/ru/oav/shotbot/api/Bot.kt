package ru.oav.shotbot.api

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.oav.shotbot.config.BotProperty
import ru.oav.shotbot.config.GameWebSocketHandler
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
                when (messageText) {
                    "/start" -> "Вилли этому господину!"
                    "/fill_left" -> {
                        gameWebSocketHandler.send(GameEvent.FILL_LEFT)
                        "Наливаем слева"
                    }

                    "/fill_right" -> {
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

    private fun sendNotification(chatId: Long, responseText: String) {
        val responseMessage = SendMessage(chatId.toString(), responseText)
        responseMessage.enableMarkdown(true)
        execute(responseMessage)
    }
}
