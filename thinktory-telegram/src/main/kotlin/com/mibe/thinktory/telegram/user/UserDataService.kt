package com.mibe.thinktory.telegram.user

import com.mibe.thinktory.service.concept.Concept
import eu.vendeli.tgbot.TelegramBot

interface UserDataService {

    fun createConcept(telegramId: Long): Concept

}