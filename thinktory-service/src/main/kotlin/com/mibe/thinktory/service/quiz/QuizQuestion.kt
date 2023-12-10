package com.mibe.thinktory.service.quiz

import org.bson.types.ObjectId

class QuizQuestion(
    val question: String,
    val conceptId: ObjectId
) {

}