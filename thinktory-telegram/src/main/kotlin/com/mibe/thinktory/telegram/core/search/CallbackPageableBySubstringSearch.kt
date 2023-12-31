package com.mibe.thinktory.telegram.core.search

import com.mibe.thinktory.telegram.chat.ChatDataService
import com.mibe.thinktory.telegram.chat.DialogStackFrame
import com.mibe.thinktory.telegram.message.MessageService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.types.User

abstract class CallbackPageableBySubstringSearch<T, Q>(
    messageService: MessageService,
    bot: TelegramBot,
    resultRenderer: (T) -> String,
    uniqueSearchKey: String,
    private val resultExtractor: (T) -> String,
    private val chatDataService: ChatDataService
) : PageableBySubstringSearch<T, Q>(
    messageService, bot, resultRenderer, uniqueSearchKey
) {

    protected open suspend fun searchAndReturnResult(user: User, callbackCommand: String) {
        chatDataService.pushFrame(user.id, DialogStackFrame(callbackCommand))
        startSearch(user)
    }

    override fun getResultLink(userId: Long, result: T?): String {
        val resultString = result?.let { resultExtractor(it) }
        val link = (chatDataService.peekFrame(userId)?.command ?: "mainMenu")
        return if (resultString == null) {
            return link
        } else {
            "${link}?searchResult=${resultString}"
        }
    }

}