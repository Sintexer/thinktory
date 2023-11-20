package com.mibe.thinktory.telegram.concept

import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.concept.ConceptService
import com.mibe.thinktory.telegram.user.UserDataKeys
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.annotations.ParamMapping
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

    @CommandHandler(["/newconcept", "newconcept"])
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

    @CommandHandler(["addTitleToConcept"])
    suspend fun addTitleAndDescription(user: User, bot: TelegramBot) {
        message { "Send title you like" }.send(user, bot)
        bot.inputListener.set(user.id, "titleInput")
    }

    @InputHandler(["titleInput"])
    suspend fun titleInputCatch(update: ProcessedUpdate, user: User, bot: TelegramBot) {
        val conceptId = bot.userData.get<ObjectId>(user.id, UserDataKeys.ACTIVE_CONCEPT_ID)
        if (conceptId == null) {
            message { "I do not understand which concept you are trying to edit. Try to pick a concept first" }.send(user, bot)
            return
        }
        conceptService.updateTitle(conceptId, update.text)
        message { "Title updated. What to do next?" }
            .inlineKeyboardMarkup (newConceptKeyboard)
            .send(user, bot)
    }

    @CommandHandler(["addTopicToConcept"])
    suspend fun addTopic(user: User, bot: TelegramBot) {
        message { "Send topic name" }.send(user, bot)
        bot.inputListener.set(user.id, "topicInput")
    }

    @InputHandler(["topicInput"])
    suspend fun topicInputCatch(update: ProcessedUpdate, user: User, bot: TelegramBot) {
        val conceptId = bot.userData.get<ObjectId>(user.id, UserDataKeys.ACTIVE_CONCEPT_ID)
        if (conceptId == null) {
            message { "I do not understand which concept you are trying to edit. Try to pick a concept first" }.send(user, bot)
            return
        }
        conceptService.updateTopic(conceptId, update.text)
        message { "Topic updated. What to do next?" }
            .inlineKeyboardMarkup (newConceptKeyboard)
            .send(user, bot)
    }

    @CommandHandler(["finishConcept"])
    suspend fun finishConcept(user: User, bot: TelegramBot) {
        message { "Concept created" }
            .inlineKeyboardMarkup(finishConceptKeyboard)
            .send(user, bot)
        bot.inputListener.set(user.id, "topicInput")
    }

    private val finishConceptKeyboard: InlineKeyboardMarkupBuilder.() -> Unit = {
        "View created concept" callback "viewConcept"
        "Create another concept" callback "newconcept"
    }

    @CommandHandler(["viewConcept"])
    suspend fun viewConcept(@ParamMapping("conceptId") conceptId: ObjectId?, user: User, bot: TelegramBot) {
        if (conceptId != null) {
            sendConcept(conceptService.getById(conceptId), user, bot)
            return
        }

        sendConceptFromUserData(user, bot)
    }

    private suspend fun ConceptController.sendConceptFromUserData(
        user: User,
        bot: TelegramBot
    ) {
        val concept = getCurrentConcept(user, bot)
        if (concept == null) {
            message { "Pick a concept first" }.send(user, bot)
            return
        }
        sendConcept(concept, user, bot)
    }

    private suspend fun sendConcept(
        concept: Concept,
        user: User,
        bot: TelegramBot
    ) {
        message(getMarkdnownRender(concept))
            .send(user, bot)
    }

    @CommandHandler(["/lastconcept"])
    suspend fun lastConcept(user: User, bot: TelegramBot) {
        // TODO get by ACTIVE_CONCEPT_ID
        val recentUserConcept = conceptService.getRecentUserConcept(user.id)
        if (recentUserConcept != null) {
            message { "Last modified concept: " }.send(user, bot)
            sendConcept(recentUserConcept, user, bot)
        } else {
            message { "You don't have any concepts yet" }.send(user, bot)
        }
    }

    private fun getCurrentConcept(user: User, bot: TelegramBot): Concept? {
        val conceptId = bot.userData.get<ObjectId>(user.id, UserDataKeys.ACTIVE_CONCEPT_ID)
            ?: return null
        return conceptService.getById(conceptId)
    }
}