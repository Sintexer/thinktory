package com.mibe.thinktory.service.concept

import org.bson.types.ObjectId
import org.springframework.data.domain.Page

interface ConceptService {
    fun createConcept(bookId: ObjectId, concept: Concept): Concept
    fun createConceptFromTheory(userId: Long, theory: String): Concept
    fun getRecentUserConcept(userId: Long): Concept?
    fun updateTitle(conceptId: ObjectId, title: String): Concept
    fun updateTopic(conceptId: ObjectId, topicName: String): Concept
    fun getById(conceptId: ObjectId): Concept

    fun getAll(userId: Long, query: ConceptsQuery): Page<Concept>
}