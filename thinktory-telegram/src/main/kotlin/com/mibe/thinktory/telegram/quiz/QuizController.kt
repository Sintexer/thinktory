package com.mibe.thinktory.telegram.quiz

import com.mibe.thinktory.service.topic.TopicSearchQuery
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

    @CommandHandler(["nextConcept"])
    suspend fun nextConcept(user: User, bot: TelegramBot) {
        TODO()
    }

    @CommandHandler(["changeTopicQuery"])
    suspend fun changeTopic(user: User, bot: TelegramBot) {
        topicSearchVariants(TopicSearchQuery(0, ""), user, bot)
    }

    @InputHandler(["topicQueryInput"])
    suspend fun topicInputSearchCatch(update: ProcessedUpdate,
                                      user: User,
                                      bot: TelegramBot) {
        topicSearchVariants(TopicSearchQuery(0, update.text), user, bot)
    }

    @CommandHandler(["thisTopicsPage"])
    suspend fun thisTopicsPage(user: User, bot: TelegramBot) {
        val currentTopicsQuery = quizService.getCurrentTopicsQuery(user, bot) ?: TopicSearchQuery()
        topicSearchVariants(currentTopicsQuery, user, bot)
    }

    @CommandHandler(["nextTopicsPage"])
    suspend fun nextTopicsPage(user: User, bot: TelegramBot) {
        topicSearchVariants(quizService.getNextTopicsPageQuery(user, bot), user, bot)
    }

    @CommandHandler(["prevTopicsPage"])
    suspend fun prevTopicsPage(user: User, bot: TelegramBot) {
        topicSearchVariants(quizService.getPrevTopicsPageQuery(user, bot), user, bot)
    }

    private suspend fun topicSearchVariants(topicSearchQuery: TopicSearchQuery, user: User, bot: TelegramBot) {
        val topics = topicService.getPage(user.id, topicSearchQuery)
        quizService.setCurrentTopicsQuery(topicSearchQuery, user, bot)
        bot.inputListener.set(user.id, "topicQueryInput")
        if (topics.isEmpty) {
            message { "Can't find any topic by provided substring: ${topicSearchQuery.topicSubstring}. " +
                    "Enter another topic substring" }
                .inlineKeyboardMarkup {
                    "Go to main quiz menu" callback "quiz"
                }
                .send(user, bot)
        } else {
            message { "Found something. Choose one of these or enter another topic substring to continue search:" }
                .inlineKeyboardMarkup {
                    topics.map { it.name }.forEach {
                        it callback "changeTopic?topic=${it}"
                        br()
                    }
                    "<" callback "prevTopicsPage"
                    "pageNumber" callback "thisTopicsPage"
                    ">" callback "nextTopicsPage"
                    br()
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