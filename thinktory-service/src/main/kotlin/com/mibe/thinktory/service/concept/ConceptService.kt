package com.mibe.thinktory.service.concept

import org.bson.types.ObjectId

interface ConceptService {
    fun createConcept(bookId: ObjectId, concept: Concept): Concept
    fun createConceptFromTheory(userId: Long, theory: String): Concept
    fun getRecentUserConcept(userId: Long): Concept?
}