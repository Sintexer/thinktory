package com.mibe.thinktory.service.quiz

import org.bson.types.ObjectId

class QuizParameters(
    val userId: Long,
    val type: Quiz.Type,
    val topicId: ObjectId? = null
) {
}