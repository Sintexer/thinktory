package com.mibe.thinktory.service.topic

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface TopicRepository : MongoRepository<Topic, ObjectId> {
    fun findByName(name: String): Topic?
}