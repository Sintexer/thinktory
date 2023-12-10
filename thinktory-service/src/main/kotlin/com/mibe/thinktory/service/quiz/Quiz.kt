package com.mibe.thinktory.service.quiz

class Quiz(
    var questions: List<QuizQuestion>
) {

    fun getNextQuestion(): QuizQuestion = questions.first()

    enum class Type(
        val numberOfQuestions: Int
    ) {
        SHORT(5),
        LONG(10)
    }
}