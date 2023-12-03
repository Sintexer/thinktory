package com.mibe.thinktory.telegram.question

import com.mibe.thinktory.service.question.Question
import dev.nesk.akkurate.ValidationResult
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

class QuestionProcessorImplTest : FeatureSpec({
    val questionProcessor: QuestionProcessorImpl = QuestionProcessorImpl()
    val multiline = "question ? \n   multi line"
    val singleLine = "question Single Line"

    feature("parseQuestions") {
        withData(
            Pair("-question1\n-question2\n-question3", questions("question1", "question2", "question3")),
            Pair("question", questions("question")),
        ) {
            val parsedQuestions = questionProcessor.parseQuestions(it.first)
            parsedQuestions.action shouldBe QuestionsAction.REPLACE
            parsedQuestions.questions shouldBe it.second
        }
        scenario("multiline question near single line question") {
            val questionsBlock = "-$multiline\n-$singleLine"
            questionProcessor.parseQuestions(questionsBlock).questions shouldBe questions(multiline, singleLine)
        }
        scenario("mixed `+` and `-` syntax should fail") {
            val questionsBlock = "-$multiline\n+$singleLine"
            val exception = shouldThrow<ValidationResult.Exception> { questionProcessor.parseQuestions(questionsBlock) }
            exception.shouldHaveViolation("All questions must have same markers ('+', '-')")
        }
        scenario("several '+' as append action") {
            val questionsBlock = "+$multiline\n+$singleLine"
            val parsedQuestions = questionProcessor.parseQuestions(questionsBlock)
            parsedQuestions.action shouldBe QuestionsAction.APPEND
            parsedQuestions.questions shouldBe questions(multiline, singleLine)

        }
        scenario("several '-' as append action") {
            val questionsBlock = "-$multiline\n-$singleLine"
            val parsedQuestions = questionProcessor.parseQuestions(questionsBlock)
            parsedQuestions.action shouldBe QuestionsAction.REPLACE
            parsedQuestions.questions shouldBe questions(multiline, singleLine)

        }
        scenario("No marker on first pos") {
            val questionsBlock = "first\n-second\n+third" // will be resolved as one question
            val parsedQuestions = questionProcessor.parseQuestions(questionsBlock)
            parsedQuestions.action shouldBe QuestionsAction.REPLACE
            parsedQuestions.questions shouldBe questions(questionsBlock)
        }
    }

    feature("parseQuestion") {
        withData(
            "-$singleLine" to singleLine,
            "+$singleLine" to singleLine,
            "-$multiline" to multiline,
            "+$multiline" to multiline,
            "-$multiline\n - abba -" to "$multiline\n - abba -",
            "+$multiline\n + abba +" to "$multiline\n + abba +",
            "+$multiline\n - abba -" to "$multiline\n - abba -",
            "+$multiline\n- abba -" to "$multiline\n- abba -",
            "-$multiline\n + abba +" to "$multiline\n + abba +",
            "-$multiline\n+ abba +" to "$multiline\n+ abba +",

        ) {(source, expected) ->
            val question = questionProcessor.parseQuestion(source)
            question.content shouldBe expected
        }
    }

    feature("validateQuestion") {
        scenario("question that consists of a `-` only") {
            val result = questionProcessor.validateQuestion("-")
            result.shouldHaveViolation("Question should contain something except question markers ('+', '-')")
        }
        scenario("question that consists of a `+` only") {
            val result = questionProcessor.validateQuestion("+")
            result.shouldHaveViolation("Question should contain something except question markers ('+', '-')")
        }
        scenario("too long question") {
            val questionToLong = "a".repeat(105)
            val result = questionProcessor.validateQuestion(questionToLong)
            result.shouldHaveViolation("Question exceeds maximum length of 100")
        }
        scenario("blank question") {
            val result = questionProcessor.validateQuestion("")
            result.shouldHaveViolation("Question cannot be blank")
        }
        scenario("valid one line question") {
            val result = questionProcessor.validateQuestion(singleLine)
            shouldBeSuccess(result)
        }
        scenario("valid multiline question") {
            val result = questionProcessor.validateQuestion(multiline)
            shouldBeSuccess(result)
        }
    }
})

fun questions(vararg questions: String) = questions.map { Question(it) }.toList()

fun <T> shouldBeSuccess(result: ValidationResult<T>) = (result is ValidationResult.Success<T>) shouldBe true

fun <T> ValidationResult<T>.shouldHaveViolation(violation: String) {
    (this as ValidationResult.Failure).violations.map { it.message } shouldContain (violation)
}

fun ValidationResult.Exception.shouldHaveViolation(violation: String) {
    this.violations.map { it.message } shouldContain (violation)
}
