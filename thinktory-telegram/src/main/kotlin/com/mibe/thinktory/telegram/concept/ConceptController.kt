package com.mibe.thinktory.telegram.concept

import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.concept.ConceptService
import com.mibe.thinktory.telegram.core.CONCEPT_ICON
import com.mibe.thinktory.telegram.core.TOPIC_ICON
import com.mibe.thinktory.telegram.message.MessageService
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
class ConceptController(
    private val conceptService: ConceptService,
    private val messageService: MessageService
) {

    @CommandHandler(["/newconcept", "newconcept"])
    suspend fun newConcept(user: User, bot: TelegramBot) {
        message { "$CONCEPT_ICON Send me the theory to build concept" }.send(user, bot)
        bot.inputListener.set(user.id, "newConceptInput")
    }

    @InputHandler(["newConceptInput"])
    suspend fun newConceptInputCatch(update: ProcessedUpdate, user: User, bot: TelegramBot) {
        val concept = conceptService.createConceptFromTheory(user.id, update.text)
        bot.userData.set(user.id, UserDataKeys.ACTIVE_CONCEPT_ID, concept.id)
        messageService.sendNewMessage(user) { message{"Untitled concept created. What to do next?"} }
        sendConceptWithConfigurationKeyboard(concept.id, user)
    }

    private val newConceptKeyboard: InlineKeyboardMarkupBuilder.() -> Unit = {
        "👀 Set title" callback "setConceptTitle"
        "$TOPIC_ICON Set topic" callback "addTopicToConcept"
        "Add questions" callback "addQuestionToConcept"
        br()
        "Finish" callback "finishConcept"
    }

    @CommandHandler(["setConceptTitle"])
    suspend fun setTitle(user: User, bot: TelegramBot) {
        message { "Send title:" }.send(user, bot)
        bot.inputListener.set(user.id, "conceptTitleInput")
    }

    @InputHandler(["conceptTitleInput"])
    suspend fun titleInputCatch(update: ProcessedUpdate, user: User, bot: TelegramBot) {
        val conceptId = bot.userData.get<ObjectId>(user.id, UserDataKeys.ACTIVE_CONCEPT_ID)
        if (conceptId == null) {
            messageService.sendNewMessage(
                user,
                "I do not understand which concept you are trying to edit. Pick a concept first"
            )
            return
        }
        conceptService.updateTitle(conceptId, update.text)
        messageService.sendNewMessage(user, "Title updated")
        sendConceptWithConfigurationKeyboard(conceptId, user)
    }

    private suspend fun sendConceptWithConfigurationKeyboard(conceptId: ObjectId, user: User) {
        val concept = conceptService.getById(conceptId)
        messageService.sendNewMessage(user) {
            message(getMarkdownRender(concept))
                .inlineKeyboardMarkup(newConceptKeyboard)
        }
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
            messageService.sendNewMessage(
                user,
                "I do not understand which concept you are trying to edit. Pick a concept first"
            )
            return
        }
        conceptService.updateTopic(conceptId, update.text)
        messageService.sendNewMessage(user, "Topic updated")
        sendConceptWithConfigurationKeyboard(conceptId, user)
    }

    @CommandHandler(["finishConcept"])
    suspend fun finishConcept(user: User, bot: TelegramBot) {
        val concept = getCurrentConcept(user, bot) ?: return
        messageService.sendNewMessage(user) {
            message(getMarkdownRender(concept))
                .inlineKeyboardMarkup(finishConceptKeyboard)
        }
        bot.inputListener.set(user.id, "topicInput")
    }

    private val finishConceptKeyboard: InlineKeyboardMarkupBuilder.() -> Unit = {
        "Create another concept" callback "newconcept"
        "Go to main menu" callback "mainMenu"
    }

    @CommandHandler(["viewConcept"])
    suspend fun viewConcept(@ParamMapping("conceptId") conceptId: String?, user: User, bot: TelegramBot) {
        if (conceptId != null) {
            val concept = getCurrentConcept(user, bot) ?: return
            messageService.sendNewMessage(user) {
                message(getMarkdownRender(concept))
                    .inlineKeyboardMarkup(finishConceptKeyboard)
            }
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
        message(getMarkdownRender(concept))
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