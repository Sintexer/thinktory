package com.mibe.thinktory.telegram.concept

import com.mibe.thinktory.service.concept.ConceptService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import org.springframework.stereotype.Component

@Component
class ConceptController (
    private val conceptService: ConceptService
) {

    @CommandHandler(["/newconcept"])
    suspend fun newConcept(user: User, bot: TelegramBot) {
        message { "Send me the theory to build concept" }.send(user, bot)
        bot.inputListener.set(user.id, "newConceptInput")
    }

    @InputHandler(["newConceptInput"])
    suspend fun newConceptInputCatch(update: ProcessedUpdate, user: User, bot: TelegramBot) {
        conceptService.createConceptFromTheory(user.id, update.text)
        message { "Concept created successfully" }.send(user, bot)
    }

    @CommandHandler(["/concept"])
    suspend fun lastConcept(user: User, bot: TelegramBot) {
        val recentUserConcept = conceptService.getRecentUserConcept(user.id)
        if (recentUserConcept != null) {
            message { "Last modified concept: $recentUserConcept" }.send(user, bot)
        } else {
            message { "You don't have any concepts yet" }.send(user, bot)
        }
    }
}