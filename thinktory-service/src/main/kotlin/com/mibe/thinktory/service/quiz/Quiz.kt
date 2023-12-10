package com.mibe.thinktory.service.quiz

data class Quiz(
    var questions: List<QuizQuestion>
) {

    val ended:Boolean
        get() = questions.isEmpty()

    fun getCurrentQuestion(): QuizQuestion = questions.first()

    enum class Type(
        val numberOfQuestions: Int
    ) {
        SHORT(5),
        LONG(10)
    }
}