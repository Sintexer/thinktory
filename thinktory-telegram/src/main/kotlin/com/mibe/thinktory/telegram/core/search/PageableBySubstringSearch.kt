package com.mibe.thinktory.telegram.core.search

import com.mibe.thinktory.telegram.core.context.PagedSubstringSearchContext
import com.mibe.thinktory.telegram.message.MessageService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.utils.builders.InlineKeyboardMarkupBuilder

abstract class PageableBySubstringSearch<T, Q>(
    messageService: MessageService,
    bot: TelegramBot,
    resultRenderer: (T) -> String,
    uniqueSearchKey: String
) : PageableSearch<T, Q, PagedSubstringSearchContext>(
    messageService, bot, resultRenderer, uniqueSearchKey
) {

    protected suspend fun searchBySubstring(userId: Long, searchSubstring: String) {
        updateSearchSubstring(userId, searchSubstring)
        search(userId)
    }

    protected suspend fun resetSearch(userId: Long) {
        resetSearchSubstring(userId)
        search(userId)
    }

    override suspend fun search(userId: Long) {
        setSearchSubstringInputListener(userId)
        super.search(userId)
    }

    protected fun resetSearchSubstring(userId: Long) = updateSearchSubstring(userId, "")

    protected fun updateSearchSubstring(userId: Long, searchSubstring: String) {
        val context = getContext(userId)
        updateContext(userId, context.copy(searchSubstring = searchSubstring))
    }

    override fun updatePageIndex(userId: Long, page: Int) {
        updateContext(userId, getContext(userId).copy(page = page))
    }

    override fun getContext(userId: Long): PagedSubstringSearchContext =
        bot.chatData.get<PagedSubstringSearchContext>(userId, contextKey) ?: PagedSubstringSearchContext()

    override fun updateContext(userId: Long, context: PagedSubstringSearchContext?) =
        bot.chatData.set(userId, contextKey, context ?: PagedSubstringSearchContext())

    override fun InlineKeyboardMarkupBuilder.searchControlButtons(userId: Long) {
        resetSearchSubstringButton(userId)
        backButton(userId)
    }

    protected abstract fun InlineKeyboardMarkupBuilder.resetSearchSubstringButton(userId: Long)

    protected abstract fun setSearchSubstringInputListener(userId: Long)

}