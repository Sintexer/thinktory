package com.mibe.thinktory.service.concept

import com.mibe.thinktory.service.question.Question
import dev.nesk.akkurate.ValidationResult
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContain

class ConceptValidatorImplTest : FeatureSpec({

    val validator = ConceptValidatorImpl()

    feature("validateTitle") {

        scenario("blank") {
            { validator.validateTitle("") } shouldThrowViolation
                    "Concept title cannot be blank"
        }

        scenario("too long") {
            { validator.validateTitle("a".repeat(999)) } shouldThrowViolation
                    "Concept title length cannot exceed 250, but was 999"
        }
        scenario("arbitrary valid") {
            shouldNotThrowAny{ validator.validateTitle("a".repeat(200)) }
        }
    }

    feature("validateContent") {
        scenario("too short") {
            { validator.validateContent("") } shouldThrowViolation
                    "Concept content cannot be blank"
        }

        scenario("too long") {
            { validator.validateContent("a".repeat(701)) } shouldThrowViolation
                    "Concept content length cannot exceed 700, but was 701"
        }
        scenario("arbitrary valid") {
            shouldNotThrowAny{ validator.validateContent("a".repeat(500)) }
        }
    }

    feature("validateQuestions") {
        scenario("too many questions") {
            val questions = (0..10).map { createQuestion() };
            { validator.validateQuestions(questions) } shouldThrowViolation
                    "Concept might have at most 10 questions, but 11 questions were provided"
        }
    }

    feature("validateQuestion") {
        scenario("too short") {
            { validator.validateQuestion(Question("")) } shouldThrowViolation
                    "Concept question cannot be blank"
        }

        scenario("too long") {
            { validator.validateQuestion(Question("a".repeat(301))) } shouldThrowViolation
                    "Concept question length cannot exceed 300, but was 301"
        }
    }

    feature("validateQuery") {
        scenario("substring too long") {
            val query = ConceptsQuery(substring = "a".repeat(251));
            { validator.validateQuery(query) } shouldThrowViolation
                    "Concept query substring length cannot exceed 250, but was 251"
        }
        scenario("empty substring") {
            val query = ConceptsQuery(substring = "")
            shouldNotThrowAny { validator.validateQuery(query) }
        }
        scenario("valid substring") {
            val query = ConceptsQuery(substring = "Some not too long string");
            shouldNotThrowAny { validator.validateQuery(query) }
        }
        scenario("negative page") {
            val query = ConceptsQuery(page = -1);
            { validator.validateQuery(query) } shouldThrowViolation
                    "Page number cannot be negative"
        }
        scenario("zero valid page") {
            val query = ConceptsQuery(page = 0)
            shouldNotThrowAny { validator.validateQuery(query) }
        }
        scenario("arbitrary valid page") {
            val query = ConceptsQuery(page = 3)
            shouldNotThrowAny { validator.validateQuery(query) }
        }
        scenario("zero page size") {
            val query = ConceptsQuery(pageSize = 0);
            { validator.validateQuery(query) } shouldThrowViolation
                    "Page size must be a positive integer"
        }
        scenario("negative page size") {
            val query = ConceptsQuery(pageSize = -1);
            { validator.validateQuery(query) } shouldThrowViolation
                    "Page size must be a positive integer"
        }
        scenario("arbitrary valid page size") {
            val query = ConceptsQuery(pageSize = 3)
            shouldNotThrowAny { validator.validateQuery(query) }
        }
        scenario("page size too big") {
            val query = ConceptsQuery(pageSize = 51);
            { validator.validateQuery(query) } shouldThrowViolation
                    "Page size must be less than 50, but was 51"
        }
    }

})

fun createQuestion(): Question {
    return Question("some question")
}

infix fun (() -> Unit).shouldThrowViolation(expectedViolation: String) {
    val e = shouldThrow<ValidationResult.Exception> {
        this.invoke()
    }
    e.violations.map {it.message} shouldContain expectedViolation
}