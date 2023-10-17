package com.mibe.thinktory.telegram.concept

import com.mibe.thinktory.service.concept.ConceptService
import com.mibe.thinktory.telegram.user.UserDataKeys
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import eu.vendeli.tgbot.utils.builders.InlineKeyboardMarkupBuilder
import org.bson.types.ObjectId
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
        val concept = conceptService.createConceptFromTheory(user.id, update.text)
        bot.userData.set(user.id, UserDataKeys.ACTIVE_CONCEPT_ID, concept.id)
        message { "Concept created successfully. What to do next?" }
            .inlineKeyboardMarkup (newConceptKeyboard)
            .send(user, bot)
    }

    private val newConceptKeyboard: InlineKeyboardMarkupBuilder.() -> Unit = {
        "Set title" callback "addTitleToConcept"
        "Set topic" callback "addTopicToConcept"
        "Add questions" callback "addQuestionToConcept"
        br()
        "Finish" callback "finishConcept"
    }
//
//    @CommandHandler(["addTitleToConcept"])
//    suspend fun addTitleAndDescription(user: User, bot: TelegramBot) {
//        message { "Send title you like" }.send(user, bot)
//        bot.inputListener.set(user.id, "titleInput")
//    }
//
    @InputHandler(["titleInput"])
    suspend fun titleInputCatch(update: ProcessedUpdate, user: User, bot: TelegramBot) {
        val conceptId = bot.userData.get<ObjectId>(user.id, UserDataKeys.ACTIVE_CONCEPT_ID)
        if (conceptId == null) {
            message { "I do not understand which concept you are trying to edit. Try to pick a concept first" }.send(user, bot)
            return
        }
        conceptService.updateTitle(conceptId, update.text)
        message { "Title updated. What to do next?" }
//            .inlineKeyboardMarkup (getNewConceptEditKeyboard())
            .send(user, bot)
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