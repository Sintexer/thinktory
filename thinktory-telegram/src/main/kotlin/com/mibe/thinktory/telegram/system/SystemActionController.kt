package com.mibe.thinktory.telegram.system

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.UnprocessedHandler
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.User
import org.springframework.stereotype.Component

@Component
class SystemActionController {

    @CommandHandler(["/cancel"])
    suspend fun cancel(user: User, bot: TelegramBot) {
        bot.inputListener.set(user.id, "")
    }

    @UnprocessedHandler
    suspend fun unhandledUpdates(user: User, bot: TelegramBot) {
        message { "The bot does not understand what you want him to do :(" }.send(user, bot)
    }

}