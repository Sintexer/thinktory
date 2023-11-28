package com.mibe.thinktory.telegram.topic

import com.mibe.thinktory.service.topic.Topic
import com.mibe.thinktory.service.topic.TopicSearchQuery
import com.mibe.thinktory.service.topic.TopicService
import com.mibe.thinktory.telegram.chat.ChatDataService
import com.mibe.thinktory.telegram.core.context.PagedSubstringSearchContext
import com.mibe.thinktory.telegram.core.search.CallbackPageableBySubstringSearch
import com.mibe.thinktory.telegram.message.MessageService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.annotations.ParamMapping
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import eu.vendeli.tgbot.utils.builders.InlineKeyboardMarkupBuilder
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

private const val TOPIC_SEARCH_RESULT_MESSAGE_TEXT =
    "Choose one of these or enter title substring to narrow the search results:"

@Component
class TopicCallbackSearch(
    private val topicService: TopicService,
    messageService: MessageService,
    chatDataService: ChatDataService,
    bot: TelegramBot
) : CallbackPageableBySubstringSearch<Topic, TopicSearchQuery>(
    messageService, bot, { it.name }, { it.name }, "topicSearch", chatDataService
) {

    @CommandHandler(["searchTopicAndReturnResult"])
    public override suspend fun searchAndReturnResult(user: User, callbackCommand: String, contextualData: Any?) {
        super.searchAndReturnResult(user, callbackCommand, contextualData)
    }

    override fun InlineKeyboardMarkupBuilder.resetSearchSubstringButton(userId: Long) {
        "Reset search substring" callback "resetTopicSearch"
    }

    @CommandHandler(["resetTopicSearch"])
    suspend fun resetSearch(user: User) {
        resetSearchSubstring(user.id)
        search(user.id)
    }

    override fun setSearchSubstringInputListener(userId: Long) {
        bot.inputListener.set(userId, "topicSearchSubstringInput")
    }

    @InputHandler(["topicSearchSubstringInput"])
    suspend fun topicSearchSubstringInput(update: ProcessedUpdate, user: User) {
        updateSearchSubstring(user.id, update.text)
        search(user.id)
    }

    override fun getPage(userId: Long, query: TopicSearchQuery): Page<Topic> {
        return topicService.getPage(userId, query)
    }

    override fun getPageUrl(page: Int): String {
        return "topicSearch?page=$page"
    }

    @CommandHandler(["topicSearch"])
    suspend fun searchFromPage(
        @ParamMapping("page")
        page: Int? = 0,
        user: User
    ) {
        searchFromPage(user.id, page ?: 0)
    }

    override fun getResultMessage(page: Page<Topic>): String = TOPIC_SEARCH_RESULT_MESSAGE_TEXT

    override fun toQuery(context: PagedSubstringSearchContext?): TopicSearchQuery {
        return context?.let { TopicSearchQuery(it.page, it.searchSubstring) } ?: TopicSearchQuery()
    }

    override fun getEmptySearchResultText(): String = "Cannot find any topic"

}