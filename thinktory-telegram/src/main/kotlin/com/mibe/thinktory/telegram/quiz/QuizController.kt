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
) {

    @CommandHandler(["/quiz", "quiz"])
    suspend fun quiz(user: User, bot: TelegramBot) {

    }

}