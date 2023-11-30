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

private const val CANNOT_FIND_ACTIVE_CONCEPT_MESSAGE = "Can't find active concept. Try choosing concept via search"

@Component
class ConceptController(
    private val conceptService: ConceptService,
    private val messageService: MessageService,
    private val bot: TelegramBot
) {

    @CommandHandler(["/newconcept", "newConcept"])
    suspend fun newConcept(user: User) {
        messageService.sendNewMessage(user) {
            message { "$CONCEPT_ICON Send me the theory to build concept" }
        }
        bot.inputListener.set(user.id, "newConceptInput")
    }

    @InputHandler(["newConceptInput"])
    suspend fun newConceptInputCatch(update: ProcessedUpdate, user: User) {
        val concept = conceptService.createConceptFromTheory(user.id, update.text)
        setActiveConcept(user.id, concept.id)
        messageService.sendNewMessage(user) { message { "Untitled concept created. What to do next?" } }
        sendConceptWithEditButtons(user, concept.id)
    }

    private fun setActiveConcept(userId: Long, conceptId: ObjectId) {
        bot.userData.set(userId, UserDataKeys.ACTIVE_CONCEPT_ID, conceptId)
    }

    @CommandHandler(["setConceptTitle"])
    suspend fun setTitle(user: User) {
        messageService.sendNewMessage(user, "Send title:")
        bot.inputListener.set(user.id, "conceptTitleInput")
    }

    @InputHandler(["conceptTitleInput"])
    suspend fun titleInputCatch(update: ProcessedUpdate, user: User) {
        val conceptId = getActiveConceptId(user.id)
        if (conceptId == null) {
            sendConceptNotFound(user)
            return
        }
        conceptService.updateTitle(conceptId, update.text)
        sendConceptWithEditButtons(user, conceptId)
    }

    @CommandHandler(["addTopicToConcept"])
    suspend fun addTopic(user: User) {
        message { "Send topic name" }.send(user, bot)
        bot.inputListener.set(user.id, "topicInput")
    }

    @InputHandler(["topicInput"])
    suspend fun topicInputCatch(update: ProcessedUpdate, user: User) {
        val conceptId = getActiveConceptId(user.id)
        if (conceptId == null) {
            messageService.sendNewMessage(user, CANNOT_FIND_ACTIVE_CONCEPT_MESSAGE)
            return
        }
        conceptService.updateTopic(conceptId, update.text)
        messageService.sendNewMessage(user, "Topic updated")
        sendConceptWithEditButtons(user, conceptId)
    }

    @CommandHandler(["viewConcept"])
    suspend fun viewConcept(@ParamMapping("conceptId") conceptId: String?, user: User, bot: TelegramBot) {
        val currentConceptId = conceptId?.let { ObjectId(it) } ?: getActiveConceptId(user.id)
        if (currentConceptId == null) {
            sendConceptNotFound(user)
            return
        }

        setActiveConcept(user.id, currentConceptId)
        sendConceptWithEditButtons(user, currentConceptId)
    }

    @CommandHandler(["/lastconcept", "/lastConcept"])
    suspend fun lastConcept(user: User, bot: TelegramBot) {
        val lastEditedConcept = getLastConcept(user.id)
        if (lastEditedConcept != null) {
            sendConceptWithEditButtons(user, lastEditedConcept.id)
        } else {
            sendConceptNotFound(user)
        }
    }

    private fun getLastConcept(userId: Long): Concept? {
        return getActiveConcept(userId) ?: conceptService.getLastEditedConcept(userId)
    }

    private fun getActiveConcept(userId: Long): Concept? {
        val conceptId = getActiveConceptId(userId)
            ?: return null
        return conceptService.getById(conceptId)
    }

    private fun getActiveConceptId(userId: Long) =
        bot.userData.get<ObjectId>(userId, UserDataKeys.ACTIVE_CONCEPT_ID)

    private suspend fun sendConceptWithEditButtons(user: User, conceptId: ObjectId) {
        val concept = conceptService.getById(conceptId)
        messageService.sendNewMessage(user) {
            message(getMarkdownRender(concept))
                .inlineKeyboardMarkup{conceptEditKeyboard()}
        }
    }

    private suspend fun sendConceptNotFound(user: User) {
        messageService.sendNewMessage(user) {
            message(CANNOT_FIND_ACTIVE_CONCEPT_MESSAGE)
                .inlineKeyboardMarkup{navigationButtons()}
        }
    }

    private fun InlineKeyboardMarkupBuilder.conceptEditKeyboard() {
        "👀 Set title" callback "setConceptTitle"
        "$TOPIC_ICON Set topic" callback "addTopicToConcept"
        "Add questions" callback "addQuestionToConcept"
        br()
        navigationButtons()
    }

    private fun InlineKeyboardMarkupBuilder.navigationButtons() {
        "Create another concept" callback "newConcept"
        "Go to main menu" callback "mainMenu"
    }
}
