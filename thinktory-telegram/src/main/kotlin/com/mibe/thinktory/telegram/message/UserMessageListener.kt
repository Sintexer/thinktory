package com.mibe.thinktory.telegram.message

import eu.vendeli.tgbot.types.Update
import eu.vendeli.tgbot.types.internal.ProcessedUpdate

interface UserMessageListener {
    fun onUserMessage(update: ProcessedUpdate)
}