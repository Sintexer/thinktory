package com.mibe.thinktory.telegram.question

import com.mibe.thinktory.service.question.Question

data class QuestionsParseResult(
    val questions: List<Question>,
    val action: QuestionsAction
)

fun questionsReplace(question: Question) = QuestionsParseResult(listOf(question), QuestionsAction.REPLACE)
fun questionsReplace(questions: List<Question>) = QuestionsParseResult(questions, QuestionsAction.REPLACE)
fun questionsAppend(questions: List<Question>) = QuestionsParseResult(questions, QuestionsAction.APPEND)