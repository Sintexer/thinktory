package com.mibe.thinktory.service.quiz

interface QuizService {

    fun createQuiz(parameters: QuizParameters): Quiz

    fun updateQuizOnFailure(quiz: Quiz): Quiz

}