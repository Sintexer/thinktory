package com.mibe.thinktory.telegram.start

import com.mibe.thinktory.service.book.BookService
import com.mibe.thinktory.telegram.user.UserDataKeys
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.User
import org.springframework.stereotype.Component

@Component
class StartController(
    private val bookService: BookService
) {
    @CommandHandler(["/start"])
    suspend fun start(user: User, bot: TelegramBot) {
        val bookId = "none"
        message { "Hello! Your book Id is $bookId" }.send(user, bot)
    }
}