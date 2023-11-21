package com.mibe.thinktory.telegram.topic

import com.mibe.thinktory.service.topic.Topic
import com.mibe.thinktory.service.topic.TopicSearchQuery
import com.mibe.thinktory.service.topic.TopicService
import com.mibe.thinktory.telegram.chat.ChatDataService
import com.mibe.thinktory.telegram.message.MessageService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.annotations.ParamMapping
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.MessageUpdate
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import eu.vendeli.tgbot.utils.builders.InlineKeyboardMarkupBuilder
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

private const val TOPIC_SEARCH_STRING = "topicSearchString"

private const val TOPIC_SEARCH_RESULT_MESSAGE_TEXT = "Choose one of these or enter title substring to narrow the search results:"

@Component
class TopicSelection(
    private val topicService: TopicService,
    private val messageService: MessageService,
    private val chatDataService: ChatDataService,
    private val bot: TelegramBot
) {

    @CommandHandler(["topicSelectionStart"])
    suspend fun topicSelectionStart(user: User) {
        resetSearchSubstring(user)
        val topicQuery = TopicSearchQuery(0)
        val conceptsPage = topicService.getPage(user.id, topicQuery)
        if (conceptsPage.isEmpty) {
            messageService.sendMarkupUpdateViaLastMessage(user, getEmptySearchResultText()) { emptyResultMenu(user.id) }
            return
        }

        setSearchSubstringInputListener(user)
        messageService.sendMarkupUpdateViaLastMessage(user, TOPIC_SEARCH_RESULT_MESSAGE_TEXT) {
                topicSelectButtons(user.id, conceptsPage)
                paginationButtons(conceptsPage)
                searchControlButtons(user.id)
        }
    }

    private fun resetSearchSubstring(user: User) = setSearchSubstring(user, "")

    private fun setSearchSubstring(user: User, substring: String) {
        bot.chatData[user, TOPIC_SEARCH_STRING] = substring
    }

    private fun getSearchSubstring(user: User): String {
        return bot.chatData[user, TOPIC_SEARCH_STRING] ?: ""
    }

    private fun getEmptySearchResultText() = "Can't find any topic by provided parameters"

    private fun InlineKeyboardMarkupBuilder.emptyResultMenu(userId: Long): InlineKeyboardMarkupBuilder =
        "Go back" callback getResultLink(userId)

    private fun setSearchSubstringInputListener(user: User) {
        bot.inputListener[user] = "topicSearchSubstring"
    }

    @InputHandler(["topicSearchSubstring"])
    suspend fun topicSearchSubstring(update: MessageUpdate, user: User) {
        val searchSubstring = update.text
        setSearchSubstring(user, searchSubstring)
        topicsSearch(user)
    }

    @CommandHandler(["clearSearchSubstring"])
    suspend fun clearSearchSubstring(update: ProcessedUpdate, user: User) {
        resetSearchSubstring(user)
        topicsSearch(user)
    }

    @CommandHandler(["topicSelection"])
    suspend fun topicsSearch(
        @ParamMapping("page")
        page: Int? = 0,
        user: User
    ) {
        topicsSearch(user, page ?: 0)
    }

    private suspend fun topicsSearch(
        user: User,
        page: Int = 0
    ) {
        val query = getQuery(user, page)
        setSearchSubstringInputListener(user)
        val topicsPage = topicService.getPage(user.id, query)
        if (topicsPage.isEmpty) {
            messageService.sendMarkupUpdateViaLastMessage(user, getEmptySearchResultText()) {
                emptyResultMenu(user.id)
            }
            return
        }

        messageService.sendMarkupUpdateViaLastMessage(user, TOPIC_SEARCH_RESULT_MESSAGE_TEXT) {
            topicSelectButtons(user.id, topicsPage)
            paginationButtons(topicsPage)
            searchControlButtons(user.id)
        }
    }

    private fun getQuery(user: User, page: Int? = 0) = TopicSearchQuery(page ?: 0, getSearchSubstring(user))

    private fun InlineKeyboardMarkupBuilder.topicSelectButtons(userId: Long, topicsPage: Page<Topic>) {
        topicsPage.forEach { topic ->
            topic.name callback getResultLink(userId, topic.id.toString())
            br()
        }
    }

    private fun getOutParameterName(userId: Long) = chatDataService.getTopicRequest(userId)?.outParameterName

    private fun InlineKeyboardMarkupBuilder.paginationButtons(
        topicsPage: Page<Topic>
    ) {
        val currentPage = topicsPage.number
        val maxPage = topicsPage.totalPages

        prevTopicPageButton(currentPage)
        currentOfTotalPageButton(currentPage, maxPage)
        nextTopicPageButton(currentPage, maxPage)
        br()
    }

    private fun InlineKeyboardMarkupBuilder.currentOfTotalPageButton(
        currentPage: Int,
        maxPage: Int
    ): InlineKeyboardMarkupBuilder {
        return "${currentPage + 1} / $maxPage" callback getTopicSelectionUrl(currentPage)
    }

    private fun InlineKeyboardMarkupBuilder.prevTopicPageButton(currentPage: Int): InlineKeyboardMarkupBuilder {
        return if (currentPage == 0) {
            "No prev page" callback getTopicSelectionUrl(currentPage)
        } else {
            "<" callback getTopicSelectionUrl(currentPage - 1)
        }
    }

    private fun InlineKeyboardMarkupBuilder.nextTopicPageButton(
        currentPage: Int,
        maxPage: Int
    ): InlineKeyboardMarkupBuilder {
        return if (currentPage + 1 < maxPage) {
            ">" callback getTopicSelectionUrl(currentPage + 1)
        } else {
            "No next page" callback getTopicSelectionUrl(currentPage)
        }
    }

    private fun getTopicSelectionUrl(page: Int): String {
        return "topicSelection?page=${page}"
    }

    private fun getResultLink(userId: Long, outParameterValue: String? = null): String {
        val link = (chatDataService.peekFrame(userId)?.command ?: "mainMenu")
        return if (outParameterValue == null) {
            link
        } else {
            "${link}?${getOutParameterName(userId)}=${outParameterValue}"
        }
    }

    private fun InlineKeyboardMarkupBuilder.searchControlButtons(userId: Long) {
        "Clear search string" callback "clearSearchSubstring"
        "Back" callback getResultLink(userId)
    }

}