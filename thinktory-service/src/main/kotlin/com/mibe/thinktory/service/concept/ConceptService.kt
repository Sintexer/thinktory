package com.mibe.thinktory.service.concept

import org.bson.types.ObjectId
import org.springframework.data.domain.Page

interface ConceptService {
    fun createConceptFromTitle(userId: Long, title: String): Concept
    fun getLastEditedConcept(userId: Long): Concept?
    fun updateContent(conceptId: ObjectId, content: String): Concept
    fun updateTitle(conceptId: ObjectId, title: String): Concept
    fun updateTopic(conceptId: ObjectId, topicName: String): Concept
    fun getById(conceptId: ObjectId): Concept

    fun getPage(userId: Long, query: ConceptsQuery): Page<Concept>
}