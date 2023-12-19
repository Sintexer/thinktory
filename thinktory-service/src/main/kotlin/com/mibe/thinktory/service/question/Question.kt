package com.mibe.thinktory.service.question

import dev.nesk.akkurate.annotations.Validate

@Validate
data class Question(
    val content: String
)