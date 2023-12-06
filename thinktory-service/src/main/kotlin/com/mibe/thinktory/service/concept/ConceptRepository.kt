package com.mibe.thinktory.service.concept

import org.bson.types.ObjectId
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.data.mongodb.repository.MongoRepository

interface ConceptRepository : MongoRepository<Concept, ObjectId> {
    fun findTopByUserIdOrderByLastUpdateDesc(userId: Long): Concept?
}