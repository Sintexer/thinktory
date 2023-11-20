package com.mibe.thinktory.telegram.quiz

import com.mibe.thinktory.service.topic.Topic
import com.mibe.thinktory.service.topic.TopicSearchQuery
import com.mibe.thinktory.service.topic.nextPage
import com.mibe.thinktory.service.topic.prevPage
import com.mibe.thinktory.telegram.user.UserDataKeys
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.types.User
import org.springframework.stereotype.Service

@Service
class QuizService {

    fun getCurrentTopicOrNull(user: User, bot: TelegramBot): Topic? {
        return bot.userData.get<Topic>(user.id, UserDataKeys.ACTIVE_TOPIC)
    }

    fun setCurrentTopicsQuery(topicSearchQuery: TopicSearchQuery, user: User, bot: TelegramBot) {
        bot.userData.set(user.id, UserDataKeys.CURRENT_TOPICS_QUERY, topicSearchQuery)
    }

    fun getCurrentTopicsQuery(user: User, bot: TelegramBot): TopicSearchQuery? {
        return bot.userData.get<TopicSearchQuery>(user.id, UserDataKeys.CURRENT_TOPICS_QUERY)
    }

    fun getNextTopicsPageQuery(user: User, bot: TelegramBot): TopicSearchQuery {
        return getCurrentTopicsQuery(user, bot).nextPage()
    }

    fun getPrevTopicsPageQuery(user: User, bot: TelegramBot): TopicSearchQuery {
        return getCurrentTopicsQuery(user, bot).prevPage()
    }

}