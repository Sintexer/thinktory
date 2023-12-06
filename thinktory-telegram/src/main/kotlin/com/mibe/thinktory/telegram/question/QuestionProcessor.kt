package com.mibe.thinktory.telegram.question

import com.mibe.thinktory.service.question.Question

interface QuestionProcessor {

    fun updateQuestions(questions: List<Question>, questionsBlock: String): List<Question>
    fun parseQuestions(questionsBlock: String): QuestionsParseResult
    fun parseQuestion(question: String): Question
    fun getQuestionsDiff(questionsBefore: List<Question>, questionsAfter: List<Question>): List<Question>

}