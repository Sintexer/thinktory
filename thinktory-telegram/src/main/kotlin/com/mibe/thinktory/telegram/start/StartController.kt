package com.mibe.thinktory.telegram.start

import com.mibe.thinktory.service.book.BookService
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
        val book = bookService.getOrCreateBook(user.id)
        message { "Hello! Your book Id is ${book.id}" }.send(user, bot)
    }
}