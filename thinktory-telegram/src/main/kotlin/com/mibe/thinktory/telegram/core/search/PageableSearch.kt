package com.mibe.thinktory.telegram.core.search

import com.mibe.thinktory.telegram.core.NOTHING_ICON
import com.mibe.thinktory.telegram.core.context.PageableSearchContext
import com.mibe.thinktory.telegram.message.MessageService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.utils.builders.InlineKeyboardMarkupBuilder
import org.springframework.data.domain.Page

abstract class PageableSearch<T, Q, C : PageableSearchContext>(
    protected val messageService: MessageService,
    protected val bot: TelegramBot,
    protected val resultRenderer: (T) -> String,
    protected val uniqueSearchKey: String
) : Search<Q, C>() {

    protected val contextKey get() = "${uniqueSearchKey}Context"

    override suspend fun startSearch(user: User) {
        search(user.id)
    }

    protected open suspend fun searchFromPage(userId: Long, page: Int?) {
        updatePageIndex(userId, page ?: 0)
        search(userId)
    }

    protected open suspend fun search(userId: Long) {
        val query = getQuery(userId)
        val page = getPage(userId, query)
        if (page.isEmpty) {
            sendEmptyPage(userId)
            return
        }

        sendPage(userId, page)
    }

    protected open fun getQuery(userId: Long): Q = toQuery(getContext(userId))

    protected open suspend fun sendEmptyPage(userId: Long) {
        messageService.sendMarkupUpdateViaLastMessage(getEmptySearchResultText(), {
            emptyResultMenu(userId)
        }, userId)
    }

    protected open fun InlineKeyboardMarkupBuilder.emptyResultMenu(userId: Long) = backButton(userId)

    protected open suspend fun sendPage(userId: Long, page: Page<T>) {
        messageService.sendMarkupUpdateViaLastMessage(getResultMessagePrefix(userId) + getResultMessageText(page), {
            selectButtons(userId, page)
            selectNone(userId)
            if (page.totalPages > 1) {
                paginationButtons(page)
            }
            customControls(userId, page)
            searchControlButtons(userId)
        }, userId)
    }


    protected open fun InlineKeyboardMarkupBuilder.customControls(userId: Long, page: Page<T>) {
    }

    protected open fun InlineKeyboardMarkupBuilder.selectNone(userId: Long) {
        "Select None" callback getResultLink(userId)
        br()
    }

    protected open fun InlineKeyboardMarkupBuilder.selectButtons(userId: Long, page: Page<T>) {
        page.forEach { result ->
            resultRenderer(result) callback getResultLink(userId, result)
            br()
        }
    }

    protected open fun InlineKeyboardMarkupBuilder.paginationButtons(
        page: Page<T>
    ) {
        val currentPage = page.number
        val maxPage = page.totalPages

        prevPageButton(currentPage)
        currentOfTotalPageButton(currentPage, maxPage)
        nextPageButton(currentPage, maxPage)
        br()
    }

    protected open fun InlineKeyboardMarkupBuilder.currentOfTotalPageButton(
        currentPage: Int,
        maxPage: Int
    ): InlineKeyboardMarkupBuilder {
        return "${currentPage + 1} / $maxPage" callback getPageUrl(currentPage)
    }

    protected open fun InlineKeyboardMarkupBuilder.prevPageButton(currentPage: Int): InlineKeyboardMarkupBuilder {
        return if (currentPage == 0) {
            "No prev page" callback getPageUrl(currentPage)
        } else {
            "<" callback getPageUrl(currentPage - 1)
        }
    }

    protected open fun InlineKeyboardMarkupBuilder.nextPageButton(
        currentPage: Int,
        maxPage: Int
    ): InlineKeyboardMarkupBuilder {
        return if (currentPage + 1 < maxPage) {
            ">" callback getPageUrl(currentPage + 1)
        } else {
            "No next page" callback getPageUrl(currentPage)
        }
    }

    protected open fun InlineKeyboardMarkupBuilder.searchControlButtons(userId: Long) {
        backButton(userId)
    }

    protected open fun InlineKeyboardMarkupBuilder.backButton(userId: Long) {
        "Back" callback getResultLink(userId)
    }

    protected abstract fun getResultLink(userId: Long, result: T? = null): String

    protected abstract fun updatePageIndex(userId: Long, page: Int)
    protected abstract fun getPage(userId: Long, query: Q): Page<T>
    protected abstract fun getEmptySearchResultText(): String
    protected abstract fun getPageUrl(page: Int): String
    protected open fun getResultMessageText(page: Page<T>): String = "Send query substring to narrow the search results:"
    protected open fun getResultMessagePrefix(userId: Long): String = ""

}