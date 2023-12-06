package com.mibe.thinktory.telegram.concept

import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.concept.ConceptService
import com.mibe.thinktory.service.question.Question
import com.mibe.thinktory.telegram.core.CONCEPT_ICON
import com.mibe.thinktory.telegram.core.CONCEPT_TITLE_ICON
import com.mibe.thinktory.telegram.core.QUESTION_ICON
import com.mibe.thinktory.telegram.core.REMOVE_ICON
import com.mibe.thinktory.telegram.core.TOPIC_ICON
import com.mibe.thinktory.telegram.core.WARNING_ICON
import com.mibe.thinktory.telegram.message.MessageService
import com.mibe.thinktory.telegram.question.ALL_MARKERS_HINT
import com.mibe.thinktory.telegram.question.QuestionProcessor
import com.mibe.thinktory.telegram.user.UserDataKeys
import dev.nesk.akkurate.ValidationResult
import dev.nesk.akkurate.constraints.ConstraintViolationSet
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
private const val CONTEXT_DATA_EXPIRED_MESSAGE =
    "I'm unable to perform and action. Probably contextual data expired, " +
            "because our last interaction was too long ago (I forgor...). Let's start from beginning"

private const val QUESTION_EDIT_DESCRIPTION =
    "Edit concept questions. Questions are plain strings, that should ask about concept. An answer to " +
            "a question should always be concept\n\n" +
            "- Editing questions means you're editing whole block of questions.\n" +
            "- Start your message with a plus (+) " +
            "to just append new question, without replacing current questions block.\n" +
            "- You can send several questions on separate lines. Each question should " +
            "start with a marker $ - otherwise it is interpreted as multiline question."

@Component
class ConceptController(
    private val conceptService: ConceptService,
    private val messageService: MessageService,
    private val questionProcessor: QuestionProcessor,
    private val bot: TelegramBot
) {

    @CommandHandler(["/newconcept", "newConcept"])
    suspend fun newConcept(user: User) {
        messageService.sendNewMessage(user) {
            message { "$CONCEPT_ICON Send me the new concept title" }
        }
        bot.inputListener.set(user.id, "newConceptInput")
    }

    @InputHandler(["newConceptInput"])
    suspend fun newConceptInputCatch(update: ProcessedUpdate, user: User) {
        val concept = conceptService.createConceptFromTitle(user.id, update.text)
        setActiveConcept(user.id, concept.id)
        messageService.sendNewMessage(user) { message { "Concept with title only created. What to do next?" } }
        sendConceptWithEditButtons(user, concept.id)
    }

    private fun setActiveConcept(userId: Long, conceptId: ObjectId) {
        bot.userData.set(userId, UserDataKeys.ACTIVE_CONCEPT_ID, conceptId)
    }

    @CommandHandler(["setConceptContent"])
    suspend fun setContent(user: User) {
        messageService.sendNewMessage(user, "Send concept body:")
        bot.inputListener.set(user.id, "conceptContentInput")
    }

    @InputHandler(["conceptContentInput"])
    suspend fun contentInputCatch(update: ProcessedUpdate, user: User) {
        val conceptId = getActiveConceptIdOrNull(user.id)
        if (conceptId == null) {
            sendContextDataExpired(user)
            return
        }
        conceptService.updateContent(conceptId, update.text)
        sendConceptWithEditButtons(user, conceptId)
    }

    @CommandHandler(["setConceptTitle"])
    suspend fun setTitle(user: User) {
        messageService.sendNewMessage(user, "Send title:")
        bot.inputListener.set(user.id, "conceptTitleInput")
    }

    @InputHandler(["conceptTitleInput"])
    suspend fun titleInputCatch(update: ProcessedUpdate, user: User) {
        val conceptId = getActiveConceptIdOrNull(user.id)
        if (conceptId == null) {
            sendContextDataExpired(user)
            return
        }
        conceptService.updateTitle(conceptId, update.text)
        sendConceptWithEditButtons(user, conceptId)
    }

    @CommandHandler(["addTopicToConcept"])
    suspend fun addTopic(user: User) {
        message { "Send topic name" }.send(user, bot)
        bot.inputListener.set(user.id, "conceptTopicInput")
    }

    @InputHandler(["conceptTopicInput"])
    suspend fun topicInputCatch(update: ProcessedUpdate, user: User) {
        val conceptId = getActiveConceptIdOrNull(user.id)
        if (conceptId == null) {
            messageService.sendNewMessage(user, CANNOT_FIND_ACTIVE_CONCEPT_MESSAGE)
            return
        }
        conceptService.updateTopic(conceptId, update.text)
        messageService.sendNewMessage(user, "Topic updated")
        sendConceptWithEditButtons(user, conceptId)
    }

    @CommandHandler(["editConceptQuestions"])
    suspend fun editConceptQuestions(user: User) {
        val concept = getActiveConceptOrNull(user.id)
        if (concept == null) {
            sendContextDataExpired(user)
            return
        }

        resetCurrentQuestionsUpdate(user.id)

        bot.inputListener.set(user.id, "conceptQuestionsInput")
        messageService.sendNewMessage(user) {
            message(concept.getQuestionsBlock()).inlineKeyboardMarkup { questionsEditMenu() }
        }
    }

    private fun InlineKeyboardMarkupBuilder.questionsEditMenu() {
        "Show questions edit hints" callback "showConceptQuestionsEditHint"
        "Go back" callback "lastConcept"
    }

    @CommandHandler(["showConceptQuestionsEditHint"])
    suspend fun showConceptQuestionsEditHint(user: User) {
        bot.inputListener.set(user.id, "conceptQuestionsInput")
        messageService.sendNewMessage(user, QUESTION_EDIT_DESCRIPTION)
    }

    @InputHandler(["conceptQuestionsInput"])
    suspend fun questionsInputCatch(update: ProcessedUpdate, user: User) {
        val concept = getActiveConceptOrNull(user.id)
        if (concept == null) {
            messageService.sendNewMessage(user, CANNOT_FIND_ACTIVE_CONCEPT_MESSAGE)
            return
        }

        val questionsBlock = update.text
        if (updateQuestions(user.id, concept, questionsBlock)) {
            sendQuestionsUpdated(user, concept.id)
        }
    }

    private suspend fun ConceptController.sendQuestionsUpdated(
        user: User,
        conceptId: ObjectId
    ) {
        messageService.sendNewMessage(user, "Questions updated")
        sendConceptWithEditButtons(user, conceptId)
    }

    private suspend fun updateQuestions(userId: Long, concept: Concept, questionsUpdate: String): Boolean {
        val resultingQuestions = try {
            questionProcessor.updateQuestions(concept.questions, questionsUpdate)
        } catch (e: ValidationResult.Exception) {
            sendConceptQuestionsUpdateFailed(userId, e.violations)
            return false
        }

        val questionsDiff = questionProcessor.getQuestionsDiff(concept.questions, resultingQuestions)
        return if (questionsDiff.isNotEmpty()) {
            verifyUserQuestionsOverwrite(userId, resultingQuestions, questionsDiff)
            false
        } else {
            conceptService.updateQuestions(concept.id, resultingQuestions)
            true
        }
    }

    private suspend fun sendConceptQuestionsUpdateFailed(userId: Long, violations: ConstraintViolationSet) {
        bot.inputListener.set(userId, "conceptQuestionsInput")
        val error = violations.joinToString(" and ") { it.message }
        messageService.sendNewMessage(userId, "Can't accept this questions block: $error\n\nYour turn:")
    }

    private suspend fun verifyUserQuestionsOverwrite(
        userId: Long,
        resultingQuestions: List<Question>,
        questionsDiff: List<Question>
    ) {
        setCurrentQuestionsUpdate(userId, resultingQuestions)
        messageService.sendNewMessage(userId) {
            message {
                "Are you sure? There are some questions, that will be removed once you press Save: \n\n" +
                        questionsDiff.joinToString("\n") { "$REMOVE_ICON ${it.content}" }
            }.inlineKeyboardMarkup { questionsOverwriteConfirmationButtons() }
        }

    }

    private fun InlineKeyboardMarkupBuilder.questionsOverwriteConfirmationButtons() {
        "$WARNING_ICON Yes, overwrite" callback "conceptQuestionsOverwriteConfirm"
        "Discard!" callback "lastConcept"
    }

    @CommandHandler(["conceptQuestionsOverwriteConfirm"])
    suspend fun conceptQuestionsOverwriteConfirm(user: User) {
        val concept = getActiveConceptOrNull(user.id)
        val questionsUpdate = getCurrentQuestionsUpdate(user.id)
        if (concept == null || questionsUpdate == null) {
            sendContextDataExpired(user)
            return
        }

        conceptService.updateQuestions(concept.id, questionsUpdate)
        sendQuestionsUpdated(user, concept.id)
    }

    private fun setCurrentQuestionsUpdate(userId: Long, questionsUpdate: List<Question>) {
        bot.userData.set(userId, UserDataKeys.CONCEPT_QUESTIONS_UPDATE, questionsUpdate)
    }

    private fun resetCurrentQuestionsUpdate(userId: Long) {
        return bot.userData.del(userId, UserDataKeys.CONCEPT_QUESTIONS_UPDATE)
    }

    private fun getCurrentQuestionsUpdate(userId: Long): List<Question>? {
        return bot.userData.get<List<Question>>(userId, UserDataKeys.CONCEPT_QUESTIONS_UPDATE)
    }

    @CommandHandler(["viewConcept"])
    suspend fun viewConcept(@ParamMapping("conceptId") conceptId: String?, user: User, bot: TelegramBot) {
        val currentConceptId = conceptId?.let { ObjectId(it) } ?: getActiveConceptIdOrNull(user.id)
        if (currentConceptId == null) {
            sendContextDataExpired(user)
            return
        }

        setActiveConcept(user.id, currentConceptId)
        sendConceptWithEditButtons(user, currentConceptId)
    }

    @CommandHandler(["/lastconcept", "/lastConcept", "lastConcept"])
    suspend fun lastConcept(user: User, bot: TelegramBot) {
        val lastEditedConcept = getLastConceptOrNull(user.id)
        if (lastEditedConcept != null) {
            sendConceptWithEditButtons(user, lastEditedConcept.id)
        } else {
            sendContextDataExpired(user)
        }
    }

    private fun getLastConceptOrNull(userId: Long): Concept? {
        return getActiveConceptOrNull(userId) ?: conceptService.getLastEditedConcept(userId)
    }

    private fun getActiveConceptOrNull(userId: Long): Concept? {
        val conceptId = getActiveConceptIdOrNull(userId)
            ?: return null
        return conceptService.getById(conceptId)
    }

    private fun getActiveConceptIdOrNull(userId: Long) =
        bot.userData.get<ObjectId>(userId, UserDataKeys.ACTIVE_CONCEPT_ID)

    private suspend fun sendConceptWithEditButtons(user: User, conceptId: ObjectId) {
        val concept = conceptService.getById(conceptId)
        messageService.sendNewMessage(user) {
            message(getMarkdownRender(concept))
                .inlineKeyboardMarkup { conceptEditKeyboard() }
        }
    }

    private suspend fun sendContextDataExpired(user: User) {
        messageService.sendNewMessage(user) {
            message(CONTEXT_DATA_EXPIRED_MESSAGE)
                .inlineKeyboardMarkup { navigationButtons() }
        }
    }

    private fun InlineKeyboardMarkupBuilder.conceptEditKeyboard() {
        "$CONCEPT_ICON Set title" callback "setConceptTitle"
        "$CONCEPT_TITLE_ICON Set content" callback "setConceptContent"
        "$TOPIC_ICON Set topic" callback "addTopicToConcept"
        br()
        "$QUESTION_ICON Set questions" callback "editConceptQuestions"
        navigationButtons()
    }

    private fun InlineKeyboardMarkupBuilder.navigationButtons() {
        "Create another" callback "newConcept"
        "Go to main menu" callback "mainMenu"
    }
}
