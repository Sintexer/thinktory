package com.mibe.thinktory.service.quiz

interface QuizService {

    fun createQuiz(parameters: QuizParameters): Quiz

    fun updateQuizOnSuccess(userId: Long, quiz: Quiz): Quiz
    fun updateQuizOnFailure(userId: Long, quiz: Quiz): Quiz

}