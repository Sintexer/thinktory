package com.mibe.thinktory.telegram.message

import com.mibe.thinktory.telegram.user.UserDataKeys
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.editText
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.interfaces.Action
import eu.vendeli.tgbot.interfaces.features.MarkupFeature
import eu.vendeli.tgbot.types.Message
import eu.vendeli.tgbot.types.Update
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import eu.vendeli.tgbot.types.internal.getOrNull
import eu.vendeli.tgbot.types.internal.userOrNull
import eu.vendeli.tgbot.utils.builders.InlineKeyboardMarkupBuilder
import eu.vendeli.tgbot.utils.processUpdate
import org.springframework.stereotype.Service

@Service
class MessageServiceImpl(
    private val bot: TelegramBot
) : MessageService, UserMessageListener {

    override fun onUserMessage(update: ProcessedUpdate) {
        val userId = update.userOrNull?.id ?: return
        resetLastMessageId(bot, userId)
    }

    override suspend fun sendNewMessage(user: User, messageSupplier: () -> Action<Message>) {
        val message = messageSupplier.invoke().sendAsync(user, bot).await().getOrNull()
        setLastMessageId(bot, message?.messageId, user.id)
    }

    override suspend fun sendMarkupUpdateViaLastMessage(
        newMessageText: String,
        markupBlock: InlineKeyboardMarkupBuilder.() -> Unit,
        userId: Long
    ) {
        val lastMessageId = getLastMessageId(bot, userId)
        val message = newOrEditMessage(lastMessageId, newMessageText)
            .inlineKeyboardMarkup(markupBlock)
            .sendAsync(userId, bot).await().getOrNull()
        setLastMessageId(bot, message?.messageId, userId)
    }

    private fun newOrEditMessage(lastMessageId: Long?, newMessageText: String): MarkupFeature<out Action<Message>> =
        if (lastMessageId == null) {
            message { newMessageText }
        } else {
            editText(lastMessageId) {newMessageText}
        }

    override fun setLastMessageId(
        bot: TelegramBot,
        messageId: Long?,
        userId: Long
    ) {
        bot.userData.set(userId, UserDataKeys.LAST_MESSAGE_ID, messageId)
    }

    override fun resetLastMessageId(
        bot: TelegramBot,
        userId: Long
    ) {
        bot.userData.del(userId, UserDataKeys.LAST_MESSAGE_ID)
    }

    override fun getLastMessageId(
        bot: TelegramBot,
        userId: Long
    ): Long? = bot.userData.get<Long>(userId, UserDataKeys.LAST_MESSAGE_ID)
}