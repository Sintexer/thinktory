package com.mibe.thinktory.telegram.start

import com.mibe.thinktory.service.book.BookService
import com.mibe.thinktory.telegram.message.MessageService
import com.mibe.thinktory.telegram.user.UserDataKeys
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.User
import org.springframework.stereotype.Component

@Component
class StartController(
    private val messageService: MessageService
) {
    @CommandHandler(["/start"])
    suspend fun start(user: User, bot: TelegramBot) {
        mainMenu(user, bot)
    }

    @CommandHandler(["/mainMenu", "/menu", "/mainmenu", "mainMenu"])
    suspend fun mainMenu(user: User, bot: TelegramBot) {
        messageService.sendNewMessage(user) {
            message("👀 Welcome to Thinktory").inlineKeyboardMarkup {
                "Create concept" callback "newconcept"
                "View concepts" callback "concepts"
            }
        }
    }
}