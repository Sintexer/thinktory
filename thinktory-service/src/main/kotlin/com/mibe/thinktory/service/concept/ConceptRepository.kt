package com.mibe.thinktory.service.concept

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ConceptRepository: MongoRepository<Concept, ObjectId> {

    fun findTopByUserIdOrderByLastUpdateDesc(userId: Long): Concept?
}