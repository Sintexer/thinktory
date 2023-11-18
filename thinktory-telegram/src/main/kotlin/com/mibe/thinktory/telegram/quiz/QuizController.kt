package com.mibe.thinktory.telegram.quiz

import com.mibe.thinktory.service.concept.ConceptService
import com.mibe.thinktory.service.topic.TopicService
import com.mibe.thinktory.telegram.user.UserDataKeys
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.annotations.ParamMapping
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import eu.vendeli.tgbot.utils.builders.InlineKeyboardMarkupBuilder
import org.springframework.stereotype.Component

@Component
class QuizController (
    private val conceptService: ConceptService,
    private val quizService: QuizService,
    private val topicService: TopicService
) {

    @CommandHandler(["/quiz", "quiz"])
    suspend fun quiz(user: User, bot: TelegramBot) {
        message{ "Current topic is ${getCurrentTopicName(user, bot)}" }
            .inlineKeyboardMarkup(quizKeyboard)
            .send(user, bot)
        bot.inputListener.set(user.id, "newConceptInput")
    }

    private fun getCurrentTopicName(user: User, bot: TelegramBot): String {
        return quizService.getCurrentTopicOrNull(user, bot)?.name ?: "unspecified"
    }

    private val quizKeyboard: InlineKeyboardMarkupBuilder.() -> Unit = {
        "Next concept" callback "nextConcept"
        "Change topic" callback "changeTopicQuery"
        "Stop quiz" callback "stopQuiz"
    }

    @CommandHandler(["changeTopicQuery"])
    suspend fun changeTopic(user: User, bot: TelegramBot) {
        topicSearchVariants("", user, bot)
    }

    @InputHandler(["topicQueryInput"])
    suspend fun topicInputSearchCatch(update: ProcessedUpdate, user: User, bot: TelegramBot) {
        topicSearchVariants(update.text, user, bot)
    }

    private suspend fun topicSearchVariants(topicSubstring: String, user: User, bot: TelegramBot) {
        val topics = topicService.getTopicsBySubstring(user.id, topicSubstring)
        bot.inputListener.set(user.id, "topicQueryInput")
        if (topics.isEmpty()) {
            message { "Can't find any topic by provided substring. Enter another topic substring" }
                .inlineKeyboardMarkup {
                    "Go to main quiz menu" callback "quiz"
                }
                .send(user, bot)
            return
        } else {
            message { "Found something. Choose one of these or enter another topic substring to continue search:" }
                .inlineKeyboardMarkup {
                    topics.map { it.name }.forEach {
                        it callback "changeTopic?topic=${it}"
                        br()
                    }
                    "Go to main quiz menu" callback "quiz"
                }
                .send(user, bot)
        }
    }

    @CommandHandler(["changeTopic"])
    suspend fun topicInputCatch(@ParamMapping("topic") topic: String, user: User, bot: TelegramBot) {
        bot.userData.set(user.id, UserDataKeys.ACTIVE_TOPIC, topicService.getTopicByName(user.id, topic))
        message { "Changed topic to $topic" }
        quiz(user, bot)
    }
}