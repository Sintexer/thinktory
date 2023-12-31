package com.mibe.thinktory.telegram.message

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.interfaces.Action
import eu.vendeli.tgbot.types.Message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.utils.builders.EntitiesContextBuilder
import eu.vendeli.tgbot.utils.builders.InlineKeyboardMarkupBuilder

interface MessageService {

    suspend fun sendNewMessage(userId: Long, messageSupplier: String)
    suspend fun sendNewMessage(userId: Long, messageSupplier: () -> Action<Message>)
    suspend fun sendNewMessage(user: User, messageSupplier: () -> Action<Message>)
    suspend fun sendNewMessage(user: User, messageSupplier: String)

    /**
     * Tries to edit last message markup or sends a new one
     */
    suspend fun sendMarkupUpdateViaLastMessage(
        newMessageText: String = "",
        markupBlock: InlineKeyboardMarkupBuilder.() -> Unit,
        userId: Long
    )

    fun setLastMessageId(
        bot: TelegramBot,
        messageId: Long?,
        userId: Long
    )

    fun resetLastMessageId(
        bot: TelegramBot,
        userId: Long
    )

    fun getLastMessageId(
        bot: TelegramBot,
        userId: Long
    ): Long?
}