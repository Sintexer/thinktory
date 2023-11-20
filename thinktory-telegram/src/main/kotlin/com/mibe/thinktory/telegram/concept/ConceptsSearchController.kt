package com.mibe.thinktory.telegram.concept

import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.concept.ConceptService
import com.mibe.thinktory.service.concept.ConceptsQuery
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.ParamMapping
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.utils.builders.InlineKeyboardMarkupBuilder
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class ConceptsSearchController(
    private val conceptService: ConceptService
) {

    @CommandHandler(["/concepts", "concepts"])
    suspend fun conceptsSearch(@ParamMapping("page") page: Int? = 0, user: User, bot: TelegramBot) {
        conceptsSearchByQuery(ConceptsQuery(page ?: 0), user, bot)
    }

    private suspend fun conceptsSearchByQuery(query: ConceptsQuery, user: User, bot: TelegramBot) {
        val conceptsPage = conceptService.getAll(user.id)
//      TODO concept substring input //  bot.inputListener.set(user.id, "topicQueryInput")
        if (conceptsPage.isEmpty) {
            replyPageIsEmpty(query, user, bot)
            return
        }

        message { "Found something. Choose one of these or enter another topic substring to continue search:" }
            .inlineKeyboardMarkup {
                conceptViewButtons(conceptsPage)
                paginationButtons(conceptsPage)
                "Back to the main menu" callback "mainMenu"
            }
            .send(user, bot)

    }

    private fun InlineKeyboardMarkupBuilder.conceptViewButtons(conceptsPage: Page<Concept>) {
        conceptsPage.forEach { concept ->
            concept.getShortPreviewString() callback "viewConcept?conceptId=${concept.id}"
            br()
        }
    }

    private fun InlineKeyboardMarkupBuilder.paginationButtons(
        conceptsPage: Page<Concept>
    ) {
        val currentPage = conceptsPage.number
        val maxPage = conceptsPage.totalPages

        prevConceptPageButton(currentPage)
        currentOfTotalConceptPageButton(currentPage, maxPage)
        nextConceptPageButton(currentPage, maxPage)
        br()
    }

    private fun InlineKeyboardMarkupBuilder.currentOfTotalConceptPageButton(
        currentPage: Int,
        maxPage: Int
    ): InlineKeyboardMarkupBuilder {
        return "${currentPage + 1} / ${maxPage}" callback getConceptsUrl(currentPage)
    }

    private fun InlineKeyboardMarkupBuilder.prevConceptPageButton(
        currentPage: Int
    ): InlineKeyboardMarkupBuilder {
        return if (currentPage == 0) {
            "No prev page" callback getConceptsUrl(currentPage)
        } else {
            "<" callback getConceptsUrl(currentPage - 1)
        }
    }

    private fun InlineKeyboardMarkupBuilder.nextConceptPageButton(
        currentPage: Int,
        maxPage: Int
    ): InlineKeyboardMarkupBuilder {
        return if (currentPage + 1 < maxPage) {
            ">" callback getConceptsUrl(currentPage + 1)
        } else {
            "No next page" callback getConceptsUrl(currentPage)
        }
    }

    private fun getConceptsUrl(page: Int): String = "concepts?page=${page}"

    private suspend fun replyPageIsEmpty(query: ConceptsQuery, user: User, bot: TelegramBot) {
        message {
            "Can't find any concept by provided substring: ${"TODO()"}. " +
                    "Enter another concept title query"
        }.inlineKeyboardMarkup {
            "Back to the main menu" callback "mainMenu"
        }.send(user, bot)
    }
}