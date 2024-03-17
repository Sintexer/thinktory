package com.mibe.thinktory.service.concept

import com.mibe.thinktory.service.question.Question

interface ConceptValidator {
    fun validateConcept(concept: Concept)
    fun validateTitle(title: String)
    fun validateContent(content: String)
    fun validateQuestions(questions: List<Question>)
    fun validateQuestion(question: Question)
    fun validateQuery(query: ConceptsQuery)
    fun validateId(conceptId: Long)
}