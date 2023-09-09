package com.mibe.thinktory.telegram.bot

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.core.ChatDataMapImpl
import eu.vendeli.tgbot.core.UserDataMapImpl
import eu.vendeli.tgbot.interfaces.UserData
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ThinktoryBot(
        @Value("\${thinktory.telegram.botToken}")
        private val botToken: String,
        private val springClassManager: SpringClassManager
) {

    @Bean
    fun bot(): TelegramBot {
        val bot = TelegramBot(botToken, "com.mibe.thinktory.telegram") {
            classManager = springClassManager
            context {
                userData = UserDataMapImpl
                chatData = ChatDataMapImpl
            }
        }

        return bot
    }
}