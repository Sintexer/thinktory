package com.mibe.thinktory.telegram.bot

import eu.vendeli.tgbot.TelegramBot
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService

@Component
class BotRunner(
        private val bot: TelegramBot,
        @Qualifier("botExecutorService") private val botExecutorService: ExecutorService
) {

    @EventListener(ApplicationReadyEvent::class)
    fun startBot() {
        botExecutorService.submit{
            runBlocking { bot.handleUpdates() }
        }
    }
}