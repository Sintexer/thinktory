package com.mibe.thinktory.telegram.message

import com.mibe.thinktory.telegram.user.UserDataKeys
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.editMarkup
import eu.vendeli.tgbot.api.editText
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.interfaces.Action
import eu.vendeli.tgbot.interfaces.features.MarkupFeature
import eu.vendeli.tgbot.types.Message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.getOrNull
import eu.vendeli.tgbot.utils.builders.InlineKeyboardMarkupBuilder
import org.springframework.stereotype.Service

@Service
class MessageServiceImpl(
    private val bot: TelegramBot
) : MessageService {

    override suspend fun sendNewMessage(user: User, messageSupplier: () -> Action<Message>) {
        val message = messageSupplier.invoke().sendAsync(user, bot).await().getOrNull()
        setLastMessageId(bot, user, message?.messageId)
    }

    override suspend fun sendMarkupUpdateViaLastMessage(
        user: User,
        newMessageText: String,
        markupBlock: InlineKeyboardMarkupBuilder.() -> Unit
    ) {
        val lastMessageId = getLastMessageId(bot, user)
        val message = newOrEditMessage(lastMessageId, newMessageText)
            .inlineKeyboardMarkup(markupBlock)
            .sendAsync(user, bot).await().getOrNull()

        setLastMessageId(bot, user, message?.messageId)
    }

    private fun newOrEditMessage(lastMessageId: Long?, newMessageText: String): MarkupFeature<out Action<Message>> =
        if (lastMessageId == null) {
            message { newMessageText }
        } else {
            editText(lastMessageId) {newMessageText}
        }

    override suspend fun setLastMessageId(
        bot: TelegramBot,
        user: User,
        messageId: Long?
    ) {
        bot.userData.set(user.id, UserDataKeys.LAST_MESSAGE_ID, messageId)
    }

    override suspend fun getLastMessageId(
        bot: TelegramBot,
        user: User
    ): Long? = bot.userData.get<Long>(user.id, UserDataKeys.LAST_MESSAGE_ID)
}