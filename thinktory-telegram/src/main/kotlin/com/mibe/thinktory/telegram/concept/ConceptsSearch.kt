package com.mibe.thinktory.telegram.concept

import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.concept.ConceptService
import com.mibe.thinktory.service.concept.ConceptsQuery
import com.mibe.thinktory.service.topic.Topic
import com.mibe.thinktory.service.topic.TopicService
import com.mibe.thinktory.telegram.chat.ChatDataService
import com.mibe.thinktory.telegram.core.CONCEPT_ICON
import com.mibe.thinktory.telegram.core.SEARCH_ICON
import com.mibe.thinktory.telegram.core.TOPIC_ICON
import com.mibe.thinktory.telegram.core.context.PagedSubstringSearchContext
import com.mibe.thinktory.telegram.core.search.PageableBySubstringSearch
import com.mibe.thinktory.telegram.message.MessageService
import com.mibe.thinktory.telegram.topic.TopicCallbackSearch
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.annotations.ParamMapping
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import eu.vendeli.tgbot.utils.builders.InlineKeyboardMarkupBuilder
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

private const val CONCEPTS_SEARCH_RESULT_MESSAGE_TEXT =
    "Choose one of these or send substring to narrow the search results:"
private const val CONCEPT_SEARCH_TOPIC_PARAM = "conceptSearchTopicParam"

@Component
class ConceptsSearch(
    private val conceptService: ConceptService,
    private val topicService: TopicService,
    private val chatDataService: ChatDataService,
    private val topicCallbackSearch: TopicCallbackSearch,
    messageService: MessageService,
    bot: TelegramBot
) : PageableBySubstringSearch<Concept, ConceptsQuery>(
    messageService, bot, { "$CONCEPT_ICON ${it.title}" }, "conceptSearch"
) {

    // Topic selection

    override fun InlineKeyboardMarkupBuilder.customControls(userId: Long, page: Page<Concept>) {
        getTopicButtonText(userId) callback "conceptSearchSelectTopic"
    }

    private fun getTopicButtonText(userId: Long): String {
        val selectedTopicName = getSelectedTopic(userId)?.name
        val text = if (selectedTopicName == null) "Select topic" else ": $selectedTopicName"
        return "$TOPIC_ICON $text"
    }

    @CommandHandler(["conceptSearchSelectTopic"])
    suspend fun conceptSearchSelectTopic(user: User) {
        topicCallbackSearch.searchAndReturnResult(user, "conceptSearchSelectTopicResult")
    }

    @CommandHandler(["conceptSearchSelectTopicResult"])
    suspend fun conceptSearchSelectTopicResult(@ParamMapping("searchResult") topicName: String? = null, user: User) {
        chatDataService.popFrame(user.id)
        updateSelectedTopic(user.id, topicName)
        search(user.id)
    }

    private fun updateSelectedTopic(userId: Long, topicName: String?) {
        if (topicName == null) {
            resetSelectedTopicId(userId)
            return
        }

        val topicId = topicService.getTopicByName(userId, topicName).id
        if (isNewTopic(userId, topicId)) {
            setSelectedTopicId(userId, topicId)
        }
    }

    private fun isNewTopic(
        userId: Long,
        topicId: ObjectId
    ) = getSelectedTopic(userId)?.id != topicId

    @CommandHandler(["clearTopicSelection"])
    suspend fun clearTopicSelection(user: User) {
        resetSelectedTopicId(user.id)
        search(user.id)
    }

    private fun setSelectedTopicId(userId: Long, topicId: ObjectId) {
        bot.chatData.set(userId, CONCEPT_SEARCH_TOPIC_PARAM, topicId)
    }

    private fun resetSelectedTopicId(userId: Long) {
        bot.chatData.del(userId, CONCEPT_SEARCH_TOPIC_PARAM)
    }

    private fun getSelectedTopic(userId: Long): Topic? {
        val topicId = bot.chatData.get<ObjectId>(userId, CONCEPT_SEARCH_TOPIC_PARAM)
        return topicId?.let { topicService.getTopicById(userId, topicId) }
    }

    // Search

    @CommandHandler(["/concepts", "/concept", "concepts", "concept"])
    suspend fun searchConcept(user: User) {
        search(user.id)
    }

    override fun getPage(userId: Long, query: ConceptsQuery): Page<Concept> {
        return conceptService.getPage(userId, query)
    }

    override fun toQuery(context: PagedSubstringSearchContext?): ConceptsQuery {
        return ConceptsQuery(page = context?.page ?: 0, substring = context?.searchSubstring ?: "")
    }

    override fun getPageUrl(page: Int): String {
        return "conceptSearch?page=$page"
    }

    @CommandHandler(["conceptSearch"])
    suspend fun searchFromPage(
        @ParamMapping("page")
        page: Int? = 0,
        user: User
    ) {
        searchFromPage(user.id, page)
    }

    override fun getResultMessageText(page: Page<Concept>): String {
        return CONCEPTS_SEARCH_RESULT_MESSAGE_TEXT
    }

    override fun getEmptySearchResultText(): String {
        return "Nothing found"
    }

    override fun InlineKeyboardMarkupBuilder.resetSearchSubstringButton(userId: Long) {
        "$SEARCH_ICON Reset query" callback "resetConceptSearch"
    }

    @CommandHandler(["resetConceptSearch"])
    suspend fun resetSearch(user: User) {
        resetSearch(user.id)
    }

    override fun setSearchSubstringInputListener(userId: Long) {
        bot.inputListener.set(userId, "conceptSearchSubstringInput")
    }

    @InputHandler(["conceptSearchSubstringInput"])
    suspend fun conceptSearchSubstringInput(update: ProcessedUpdate, user: User) {
        searchBySubstring(user.id, update.text)
    }

    override fun getResultLink(userId: Long, result: Concept?): String {
        return if (result == null) {
            "mainMenu"
        } else {
            "viewConcept?conceptId=${result.id}"
        }
    }
}