package ru.oav.shotbot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession


@SpringBootApplication
@ConfigurationPropertiesScan("ru.oav.shotbot.config")
class Application {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }

    @Bean
    fun telegramBotsApi(): TelegramBotsApi? {
        return TelegramBotsApi(DefaultBotSession::class.java)
    }
}
