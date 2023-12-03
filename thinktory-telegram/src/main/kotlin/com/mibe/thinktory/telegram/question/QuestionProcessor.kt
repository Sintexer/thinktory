package com.mibe.thinktory.telegram.question

import com.mibe.thinktory.service.question.Question

interface QuestionProcessor {

    fun parseQuestions(questionsBlock: String): QuestionsParseResult
    fun parseQuestion(question: String): Question
    fun validate(question: String)

}