package com.mibe.thinktory.telegram.concept

import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.concept.ConceptService
import com.mibe.thinktory.service.concept.ConceptsQuery
import com.mibe.thinktory.telegram.user.UserDataKeys
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.ParamMapping
import eu.vendeli.tgbot.api.editMarkup
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.interfaces.Action
import eu.vendeli.tgbot.types.Message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.Response
import eu.vendeli.tgbot.types.internal.getOrNull
import eu.vendeli.tgbot.utils.builders.InlineKeyboardMarkupBuilder
import kotlinx.coroutines.Deferred
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class ConceptsSearchController(
    private val conceptService: ConceptService
) {

    @CommandHandler(["/concepts", "concepts"])
    suspend fun conceptsSearch(
        @ParamMapping("page")
        page: Int? = 0,
        @ParamMapping("paginationMessageId")
        paginationMessageId: Long? = null,
        user: User,
        bot: TelegramBot
    ) {
        val query = ConceptsQuery(page ?: 0)
        val searchData = SearchData(query, paginationMessageId)
        conceptsSearchByQuery(searchData, user, bot)
    }

    private suspend fun conceptsSearchByQuery(searchData: SearchData, user: User, bot: TelegramBot) {
        val conceptsPage = conceptService.getAll(user.id, searchData.conceptsQuery)
//      TODO concept substring input //  bot.inputListener.set(user.id, "topicQueryInput")
        if (conceptsPage.isEmpty) {
            replyPageIsEmpty(searchData.conceptsQuery, user, bot)
            return
        }

        val paginationMessageId = searchData.paginationMessageId ?: getUserDataConceptSearchMessageId(bot, user)

        val a = if (paginationMessageId != null) {
            editMarkup(paginationMessageId)
                .inlineKeyboardMarkup {
                    conceptViewButtons(conceptsPage)
                    paginationButtons(conceptsPage, paginationMessageId)
                    "Back to the main menu" callback "mainMenu"
                }
        } else {
            message { "Found something. Choose one of these or enter another topic substring to continue search:" }
                .inlineKeyboardMarkup {
                    conceptViewButtons(conceptsPage)
                    paginationButtons(conceptsPage, paginationMessageId)
                    "Back to the main menu" callback "mainMenu"
                }
        }

        val sentMessage = a.sendAsync(user, bot)
        setUserDataConceptSearchMessageId(bot, user, sentMessage)

    }

    private suspend fun setUserDataConceptSearchMessageId(
        bot: TelegramBot,
        user: User,
        sentMessage: Deferred<Response<out Message>>
    ) {
        bot.userData.set(user.id, UserDataKeys.CONCEPT_SEARCH_MESSAGE_ID, sentMessage.await().getOrNull()?.messageId)
    }

    private suspend fun getUserDataConceptSearchMessageId(
        bot: TelegramBot,
        user: User
    ): Long? = bot.userData.get<Long>(user.id, UserDataKeys.CONCEPT_SEARCH_MESSAGE_ID)


    private fun InlineKeyboardMarkupBuilder.conceptViewButtons(conceptsPage: Page<Concept>) {
        conceptsPage.forEach { concept ->
            concept.getShortPreviewString() callback "viewConcept?conceptId=${concept.id}"
            br()
        }
    }

    private fun InlineKeyboardMarkupBuilder.paginationButtons(
        conceptsPage: Page<Concept>,
        paginationMessageId: Long?
    ) {
        val currentPage = conceptsPage.number
        val maxPage = conceptsPage.totalPages

        prevConceptPageButton(currentPage, paginationMessageId)
        currentOfTotalConceptPageButton(currentPage, maxPage, paginationMessageId)
        nextConceptPageButton(currentPage, maxPage, paginationMessageId)
        br()
    }

    private fun InlineKeyboardMarkupBuilder.currentOfTotalConceptPageButton(
        currentPage: Int,
        maxPage: Int,
        paginationMessageId: Long?
    ): InlineKeyboardMarkupBuilder {
        return "${currentPage + 1} / $maxPage" callback getConceptsUrl(currentPage, paginationMessageId)
    }

    private fun InlineKeyboardMarkupBuilder.prevConceptPageButton(
        currentPage: Int,
        paginationMessageId: Long?
    ): InlineKeyboardMarkupBuilder {
        return if (currentPage == 0) {
            "No prev page" callback getConceptsUrl(currentPage, paginationMessageId)
        } else {
            "<" callback getConceptsUrl(currentPage - 1, paginationMessageId)
        }
    }

    private fun InlineKeyboardMarkupBuilder.nextConceptPageButton(
        currentPage: Int,
        maxPage: Int,
        paginationMessageId: Long?
    ): InlineKeyboardMarkupBuilder {
        return if (currentPage + 1 < maxPage) {
            ">" callback getConceptsUrl(currentPage + 1, paginationMessageId)
        } else {
            "No next page" callback getConceptsUrl(currentPage, paginationMessageId)
        }
    }

    private fun getConceptsUrl(page: Int, paginationMessageId: Long?): String {
        val url = "concepts?page=${page}"

        return if (paginationMessageId != null) {
            "$url&paginationMessageId=$paginationMessageId"
        } else {
            url
        }
    }

    private suspend fun replyPageIsEmpty(query: ConceptsQuery, user: User, bot: TelegramBot) {
        message {
            "Can't find any concept by provided substring: ${"TODO()"}. " +
                    "Enter another concept title query"
        }.inlineKeyboardMarkup {
            "Back to the main menu" callback "mainMenu"
        }.send(user, bot)
    }

    private data class SearchData(
        val conceptsQuery: ConceptsQuery,
        val paginationMessageId: Long? = null
    )
}