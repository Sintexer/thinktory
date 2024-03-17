package com.mibe.thinktory.service.concept

import com.mibe.thinktory.service.concept.validation.accessors.page
import com.mibe.thinktory.service.concept.validation.accessors.pageSize
import com.mibe.thinktory.service.concept.validation.accessors.substring
import com.mibe.thinktory.service.question.Question
import com.mibe.thinktory.service.question.validation.accessors.content
import dev.nesk.akkurate.Validator
import dev.nesk.akkurate.constraints.builders.hasLengthLowerThanOrEqualTo
import dev.nesk.akkurate.constraints.builders.hasSizeLowerThanOrEqualTo
import dev.nesk.akkurate.constraints.builders.isGreaterThan
import dev.nesk.akkurate.constraints.builders.isGreaterThanOrEqualTo
import dev.nesk.akkurate.constraints.builders.isLowerThan
import dev.nesk.akkurate.constraints.builders.isNotBlank
import dev.nesk.akkurate.constraints.otherwise
import org.springframework.stereotype.Component

@Component
class ConceptValidatorImpl : ConceptValidator {

    override fun validateConcept(concept: Concept) {
        validateTitle(concept.title)
        concept.theory?.let { validateContent(concept.theory) }
//        validateQuestions(concept.questions)
    }

    override fun validateTitle(title: String) {
        validateTitle.invoke(title).orThrow()
    }

    private val validateTitle = Validator<String> {
        this {
            isNotBlank() otherwise {
                "Concept title cannot be blank"
            }
            hasLengthLowerThanOrEqualTo(MAX_CONCEPT_TITLE_LENGTH) otherwise {
                "Concept title length cannot exceed $MAX_CONCEPT_TITLE_LENGTH," +
                        " but was ${this.unwrap().length}"
            }
        }
    }

    override fun validateContent(content: String) {
        validateContent.invoke(content).orThrow()
    }

    private val validateContent = Validator<String> {
        this {
            isNotBlank() otherwise {
                "Concept content cannot be blank"
            }
            hasLengthLowerThanOrEqualTo(MAX_CONCEPT_CONTENT_LENGTH) otherwise {
                "Concept content length cannot exceed $MAX_CONCEPT_CONTENT_LENGTH," +
                        " but was ${this.unwrap().length}"
            }
        }
    }

    override fun validateQuestions(questions: List<Question>) {
        validateQuestions.invoke(questions).orThrow()
    }

    private val validateQuestions = Validator<List<Question>> {
        this {
            hasSizeLowerThanOrEqualTo(MAX_CONCEPT_QUESTIONS) otherwise {
                "Concept might have at most $MAX_CONCEPT_QUESTIONS questions, but" +
                        " ${this.unwrap().size} questions were provided"
            }
        }
        this.unwrap().forEach(::validateQuestion)
    }

    override fun validateQuestion(question: Question) {
        validateQuestion.invoke(question).orThrow()
    }

    private val validateQuestion = Validator<Question> {
        content {
            isNotBlank() otherwise {
                "Concept question cannot be blank"
            }
            hasLengthLowerThanOrEqualTo(MAX_CONCEPT_QUESTION_LENGTH) otherwise {
                "Concept question length cannot exceed $MAX_CONCEPT_QUESTION_LENGTH," +
                        " but was ${content.unwrap().length}"
            }
        }
    }

    override fun validateQuery(query: ConceptsQuery) {
        validateQuery.invoke(query).orThrow()
    }

    private val validateQuery = Validator<ConceptsQuery> {
        substring {
            hasLengthLowerThanOrEqualTo(MAX_CONCEPT_QUERY_SUBSTRING_LENGTH) otherwise {
                "Concept query substring length cannot exceed $MAX_CONCEPT_QUERY_SUBSTRING_LENGTH," +
                        " but was ${substring.unwrap().length}"
            }
        }
        page {
            isGreaterThanOrEqualTo(0) otherwise {
                "Page number cannot be negative"
            }
        }
        pageSize {
            isGreaterThan(0) otherwise {
                "Page size must be a positive integer"
            }
            isLowerThan(MAX_CONCEPT_QUERY_PAGE_SIZE) otherwise {
                "Page size must be less than $MAX_CONCEPT_QUERY_PAGE_SIZE," +
                        " but was ${pageSize.unwrap()}"
            }
        }
    }

    override fun validateId(conceptId: Long) {
        validateId.invoke(conceptId).orThrow()
    }

    private val validateId = Validator<Long> {
        this {
            isGreaterThan(0) otherwise { "Concept id mu st greater than 0, but was ${this.unwrap()}" }
        }
    }

    companion object {
        const val MAX_CONCEPT_TITLE_LENGTH = 250
        const val MAX_CONCEPT_CONTENT_LENGTH = 700
        const val MAX_CONCEPT_QUESTION_LENGTH = 300
        const val MAX_CONCEPT_QUESTIONS = 10
        const val MAX_CONCEPT_QUERY_SUBSTRING_LENGTH = MAX_CONCEPT_TITLE_LENGTH
        const val MAX_CONCEPT_QUERY_PAGE_SIZE = 50
    }
}