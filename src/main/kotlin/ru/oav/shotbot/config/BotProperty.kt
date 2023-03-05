package ru.oav.shotbot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties("bot")
data class BotProperty @ConstructorBinding constructor(val   username: String, val token: String)
