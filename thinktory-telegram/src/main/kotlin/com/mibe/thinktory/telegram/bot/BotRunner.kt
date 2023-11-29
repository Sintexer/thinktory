package com.mibe.thinktory.telegram.bot

import com.mibe.thinktory.telegram.message.UserMessageListener
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.types.Update
import eu.vendeli.tgbot.utils.processUpdate
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService

@Component
class BotRunner(
    private val bot: TelegramBot,
    @Qualifier("botExecutorService")
    private val botExecutorService: ExecutorService,
    private val userMessageListener: UserMessageListener
) {

    @EventListener(ApplicationReadyEvent::class)
    fun startBot() {
        botExecutorService.submit {
            runBlocking {
                bot.update.setListener {
                    if (isUserTextMessage(it)) {
                        userMessageListener.onUserMessage(it.processUpdate())
                    }
                    handle(it)
                }
            }
        }
    }

    fun isUserTextMessage(update: Update): Boolean = update.message != null
}