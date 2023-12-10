package com.mibe.thinktory.telegram.quiz

import com.mibe.thinktory.service.concept.ConceptService
import com.mibe.thinktory.service.quiz.Quiz
import com.mibe.thinktory.service.quiz.QuizParameters
import com.mibe.thinktory.service.quiz.QuizService
import com.mibe.thinktory.telegram.concept.getMarkdownRender
import com.mibe.thinktory.telegram.concept.getMarkdownRenderWithoutQuestions
import com.mibe.thinktory.telegram.core.CONCEPT_ICON
import com.mibe.thinktory.telegram.core.NEXT_ICON
import com.mibe.thinktory.telegram.message.MessageService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.ParamMapping
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.utils.builders.InlineKeyboardMarkupBuilder
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

@Component
class QuizController(
    private val messageService: MessageService,
    private val quizService: QuizService,
    private val conceptService: ConceptService,
    private val bot: TelegramBot
) {

    @CommandHandler(["/quiz", "quiz"])
    suspend fun quiz(user: User) {
        val quiz = getActiveQuiz(user.id)
        if (quiz == null || quiz.ended) {
            sendNewQuizMenu(user)
        } else {
            sendQuizContinueDialog(user)
        }
    }

    @CommandHandler(["quizNewMenu"])
    suspend fun sendNewQuizMenu(user: User) {
        messageService.sendNewMessage(user.id) {
            message { "Let's start a quiz" }.inlineKeyboardMarkup { quizStartMenu() }
        }
    }

    private fun InlineKeyboardMarkupBuilder.quizStartMenu() {
        "Start short quiz (5 questions)" callback "startQuiz?type=SHORT"
        br()
        "Start long quiz (10 questions)" callback "startQuiz?type=LONG"
        br()
        "Back to main menu" callback "mainMenu"
    }

    private suspend fun sendQuizContinueDialog(user: User) {
        messageService.sendNewMessage(user.id) {
            message { "I see a quiz, that wasn't finished. Do you wanna continue it?" }
                .inlineKeyboardMarkup {
                    "Yes" callback "quizNextQuestion"
                    "No" callback "quizNewMenu"
                }
        }
    }

    @CommandHandler(["/shortquiz"])
    suspend fun startShortQuiz(user: User) {
        startQuiz(Quiz.Type.SHORT, user)
    }

    @CommandHandler(["/longquiz"])
    suspend fun startLongQuiz(user: User) {
        startQuiz(Quiz.Type.LONG, user)
    }

    @CommandHandler(["startQuiz"])
    suspend fun startQuiz(@ParamMapping("type") typeString: String, user: User) {
        val type = Quiz.Type.valueOf(typeString)
        startQuiz(type, user)
    }

    suspend fun startQuiz(type: Quiz.Type, user: User) {
        val quiz = quizService.createQuiz(QuizParameters(user.id, type))
        setActiveQuiz(user.id, quiz)
        nextQuestion(user.id, quiz)
    }

    @CommandHandler(["quizNextQuestion"])
    suspend fun nextQuestion(user: User) {
        val quiz = getActiveQuiz(user.id)
        if (quiz == null) {
            sendContextDataExpired(user.id)
            return
        }
        nextQuestion(user.id, quiz)
    }

    suspend fun nextQuestion(userId: Long, quiz: Quiz) {
        if (quiz.ended) {
            sendQuizEnded(userId, quiz)
        } else {
            sendQuizQuestion(userId, quiz)
        }
    }

    private suspend fun sendQuizEnded(userId: Long, quiz: Quiz) {
        messageService.sendNewMessage(userId) {
            message("Good job. Statistics: TODO")
                .inlineKeyboardMarkup { quizEndedMenu() }
        }
    }

    private suspend fun sendQuizQuestion(userId: Long, quiz: Quiz) {
        val question = quiz.getCurrentQuestion()
        messageService.sendNewMessage(userId) {
            message(question.question).inlineKeyboardMarkup { questionMenu() }
        }
    }

    private fun InlineKeyboardMarkupBuilder.questionMenu() {
        "$CONCEPT_ICON Show concept" callback "quizShowQuestionConcept"
        "$NEXT_ICON I know it, skip" callback "quizSkipQuestion"
    }

    @CommandHandler(["quizShowQuestionConcept"])
    suspend fun quizShowQuestionConcept(user: User) {
        val quiz = getActiveQuiz(user.id)
        if (quiz == null) {
            sendContextDataExpired(user.id)
            return
        }

        val concept = conceptService.getById(quiz.getCurrentQuestion().conceptId)

        messageService.sendNewMessage(user.id) {
            message(getMarkdownRenderWithoutQuestions(concept))
                .inlineKeyboardMarkup { conceptViewButtons(concept.id) }
        }
    }

    private fun InlineKeyboardMarkupBuilder.conceptViewButtons(conceptId: ObjectId) {
        "Edit concept" callback "viewConcept?conceptId=$conceptId"
        "Got it" callback "quizConceptAnsweredCorrectly"
        "Failed" callback "quizQuestionFailed"
    }

    @CommandHandler(["quizConceptAnsweredCorrectly", "quizSkipQuestion"])
    suspend fun conceptQuestionAnswered(user: User) {
        val quiz = getActiveQuiz(user.id)
        if (quiz == null) {
            sendContextDataExpired(user.id)
            return
        }

        val updatedQuiz = quizService.updateQuizOnSuccess(user.id, quiz)
        setActiveQuiz(user.id, updatedQuiz)
        nextQuestion(user.id, updatedQuiz)
    }
    
    @CommandHandler(["quizQuestionFailed"])
    suspend fun conceptQuestionFailed(user: User) {
        val quiz = getActiveQuiz(user.id)
        if (quiz == null) {
            sendContextDataExpired(user.id)
            return
        }

        val updatedQuiz = quizService.updateQuizOnFailure(user.id, quiz)
        setActiveQuiz(user.id, updatedQuiz)
        nextQuestion(user.id, updatedQuiz)
    }

    private fun InlineKeyboardMarkupBuilder.quizEndedMenu() {
        "Start another quiz" callback "quiz"
        "Go to main menu" callback "mainMenu"
    }

    private fun getActiveQuiz(userId: Long): Quiz? {
        return bot.chatData.get<Quiz>(userId, ACTIVE_QUIZ_KEY)
    }

    private fun setActiveQuiz(userId: Long, quiz: Quiz) {
        bot.chatData.set(userId, ACTIVE_QUIZ_KEY, quiz)
    }

    private suspend fun sendContextDataExpired(userId: Long) {
        messageService.sendNewMessage(userId) {
            message(CONTEXT_DATA_EXPIRED_MESSAGE)
                .inlineKeyboardMarkup { "Go to main menu" callback "mainMenu" }
        }
    }

    companion object {
        const val ACTIVE_QUIZ_KEY = "activeQuiz"
        const val CONTEXT_DATA_EXPIRED_MESSAGE =
            "I'm unable to perform and action. Probably contextual data expired, " +
                    "because our last interaction was too long ago (I forgor...). Let's start from beginning"
    }

}