package com.mibe.thinktory.telegram.chat

data class DialogStackFrame(
    val command: String,
    val contextualData: Any? = null
)
