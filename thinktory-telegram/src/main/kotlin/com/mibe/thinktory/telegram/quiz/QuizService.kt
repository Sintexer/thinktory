package com.mibe.thinktory.telegram.quiz

import com.mibe.thinktory.service.topic.Topic
import com.mibe.thinktory.telegram.user.UserDataKeys
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.types.User
import org.springframework.stereotype.Service

@Service
class QuizService {

    fun getCurrentTopicOrNull(user: User, bot: TelegramBot): Topic? {
        return bot.userData.get<Topic>(user.id, UserDataKeys.ACTIVE_TOPIC)
    }

}