package com.mibe.thinktory.telegram.concept

import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.concept.ConceptService
import com.mibe.thinktory.service.concept.ConceptsQuery
import com.mibe.thinktory.telegram.message.MessageService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.annotations.ParamMapping
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import eu.vendeli.tgbot.utils.builders.InlineKeyboardMarkupBuilder as KeyboardBuilder

private const val CONCEPTS_SEARCH_RESULT_MESSAGE_TEXT = "Choose one of these or enter title substring to narrow the search results:"

@Component
class ConceptsSearchController(
    private val conceptService: ConceptService,
    private val messageService: MessageService,
    private val bot: TelegramBot
) {

    @CommandHandler(["/concepts"])
    suspend fun initialSearchMenuMessage(user: User) {
        val conceptsQuery = ConceptsQuery(0)
        val conceptsPage = conceptService.getPage(user.id, conceptsQuery)
        if (conceptsPage.isEmpty) {
            messageService.sendNewMessage(user) { emptyResultMessage() }
            return
        }

        setSearchSubstringInputListener(user)
        messageService.sendNewMessage(user) {
            message { CONCEPTS_SEARCH_RESULT_MESSAGE_TEXT }
                .inlineKeyboardMarkup {
                    conceptViewButtons(conceptsPage)
                    paginationButtons(conceptsPage)
                    "Back to the main menu" callback "mainMenu"
                }
        }
    }

    @InputHandler(["conceptsSearchQuery"])
    suspend fun conceptsSearchQuery(update: ProcessedUpdate, user: User) {
        conceptsSearch(user, ConceptsQuery(substring = update.text))
    }

    @CommandHandler(["concepts"])
    suspend fun conceptsSearch(
        @ParamMapping("page")
        page: Int? = 0,
        user: User
    ) {
        conceptsSearch(user, ConceptsQuery(page ?: 0))
    }

    private suspend fun conceptsSearch(
        user: User,
        query: ConceptsQuery,
    ) {
        setSearchSubstringInputListener(user)
        val conceptsPage = conceptService.getPage(user.id, query)
        if (conceptsPage.isEmpty) {
            messageService.sendMarkupUpdateViaLastMessage(user, getEmptySearchResultText()) {
                emptyResultMenu()
            }
            return
        }

        messageService.sendMarkupUpdateViaLastMessage(user, CONCEPTS_SEARCH_RESULT_MESSAGE_TEXT) {
            conceptViewButtons(conceptsPage)
            paginationButtons(conceptsPage)
            "Back to the main menu" callback "mainMenu"
        }
    }

    private fun setSearchSubstringInputListener(user: User) {
        bot.inputListener[user] = "conceptsSearchQuery"
    }

    private fun KeyboardBuilder.conceptViewButtons(conceptsPage: Page<Concept>) {
        conceptsPage.forEach { concept ->
            concept.getShortPreviewString() callback "viewConcept?conceptId=${concept.id}"
            br()
        }
    }

    private fun KeyboardBuilder.paginationButtons(
        conceptsPage: Page<Concept>
    ) {
        val currentPage = conceptsPage.number
        val maxPage = conceptsPage.totalPages

        prevConceptPageButton(currentPage)
        currentOfTotalConceptPageButton(currentPage, maxPage)
        nextConceptPageButton(currentPage, maxPage)
        br()
    }

    private fun KeyboardBuilder.currentOfTotalConceptPageButton(
        currentPage: Int,
        maxPage: Int
    ): KeyboardBuilder {
        return "${currentPage + 1} / $maxPage" callback getConceptsUrl(currentPage)
    }

    private fun KeyboardBuilder.prevConceptPageButton(currentPage: Int): KeyboardBuilder {
        return if (currentPage == 0) {
            "No prev page" callback getConceptsUrl(currentPage)
        } else {
            "<" callback getConceptsUrl(currentPage - 1)
        }
    }

    private fun KeyboardBuilder.nextConceptPageButton(
        currentPage: Int,
        maxPage: Int
    ): KeyboardBuilder {
        return if (currentPage + 1 < maxPage) {
            ">" callback getConceptsUrl(currentPage + 1)
        } else {
            "No next page" callback getConceptsUrl(currentPage)
        }
    }

    private fun getConceptsUrl(page: Int): String {
        return "concepts?page=${page}"
    }

    private fun emptyResultMessage() = message {
        getEmptySearchResultText()
    }.inlineKeyboardMarkup {
        emptyResultMenu()
    }

    private fun KeyboardBuilder.emptyResultMenu(): KeyboardBuilder =
        "Back to the main menu" callback "mainMenu"


    private fun getEmptySearchResultText() = "Can't find any concept by provided substring: ${"TODO()"}. " +
            "Enter another concept title query"
}