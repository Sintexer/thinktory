package com.mibe.thinktory.telegram.question

import com.mibe.thinktory.service.question.Question
import dev.nesk.akkurate.Validator
import dev.nesk.akkurate.constraints.builders.hasLengthGreaterThan
import dev.nesk.akkurate.constraints.builders.hasLengthGreaterThanOrEqualTo
import dev.nesk.akkurate.constraints.builders.hasLengthLowerThanOrEqualTo
import dev.nesk.akkurate.constraints.builders.isNotBlank
import dev.nesk.akkurate.constraints.builders.isNotContaining
import dev.nesk.akkurate.constraints.otherwise

const val APPEND_MARKER = "+"
const val QUESTION_ITEM_MARKER = "-"
const val ALL_MARKERS_HINT = "('+', '-')"
const val MAX_QUESTIONS_BLOCK_LENGTH = 2000
const val MAX_QUESTION_LENGTH = 100
const val MIN_QUESTION_LENGTH = 1

class QuestionProcessorImpl : QuestionProcessor {

    override fun parseQuestions(questionsBlock: String): QuestionsParseResult {
        validateQuestionsBlock(questionsBlock).orThrow()
        if (!questionsBlock.startsWithQuestionMarker()) {
            return questionsReplace(parseQuestion(questionsBlock))
        }

        val marker = getMarker(questionsBlock)
        validateQuestionMarkers(getOppositeMarker(marker), questionsBlock).orThrow()
        return parseQuestions(questionsBlock, marker)
    }

    private fun getMarker(questionsBlock: String) =
        if (questionsBlock.startsWith(APPEND_MARKER)) APPEND_MARKER else QUESTION_ITEM_MARKER

    private fun getOppositeMarker(marker: String) =
        if (marker == APPEND_MARKER) QUESTION_ITEM_MARKER else APPEND_MARKER

    val validateQuestionMarkers = Validator<String, String> { unexpectedMarker ->
        this.isNotContaining("\n$unexpectedMarker") otherwise { "All questions must have same markers $ALL_MARKERS_HINT" }
    }

    private fun parseQuestions(
        questionsBlock: String,
        delimiter: String
    ): QuestionsParseResult {
        val questions = questionsBlock.split("\n$delimiter")
            .map { parseQuestion(it) }

        return when (delimiter) {
            APPEND_MARKER -> questionsAppend(questions)
            else -> questionsReplace(questions)
        }
    }

    val validateQuestionsBlock = Validator<String> {
        this {
            isNotBlank() otherwise {
                "Questions block cannot be blank"
            }
            hasLengthLowerThanOrEqualTo(MAX_QUESTIONS_BLOCK_LENGTH) otherwise {
                "Questions block exceeds maximum length of $MAX_QUESTIONS_BLOCK_LENGTH"
            }
            if (this.unwrap().startsWithQuestionMarker()) {
                this.hasLengthGreaterThan(MIN_QUESTION_LENGTH) otherwise {
                    "Questions block should contain something except question markers $ALL_MARKERS_HINT"
                }
            }
        }
    }

    override fun parseQuestion(question: String): Question {
        validateQuestion(question).orThrow()
        val content = if (question.startsWithQuestionMarker()) {
            question.substring(1)
        } else {
            question
        }
        return Question(content)
    }

    val validateQuestion = Validator<String> {
        this {
            isNotBlank() otherwise {
                "Question cannot be blank"
            }
            hasLengthLowerThanOrEqualTo(MAX_QUESTION_LENGTH) otherwise {
                "Question exceeds maximum length of $MAX_QUESTION_LENGTH"
            }
            hasLengthGreaterThanOrEqualTo(MIN_QUESTION_LENGTH) otherwise {
                "Question should be at least $MIN_QUESTION_LENGTH characters long"
            }
        }
        if (this.unwrap().startsWithQuestionMarker()) {
            this.hasLengthGreaterThan(MIN_QUESTION_LENGTH) otherwise {
                "Question should contain something except question markers $ALL_MARKERS_HINT"
            }
        }
    }

    private fun String.startsWithQuestionMarker() =
        this.startsWith(APPEND_MARKER) || this.startsWith(QUESTION_ITEM_MARKER)

}