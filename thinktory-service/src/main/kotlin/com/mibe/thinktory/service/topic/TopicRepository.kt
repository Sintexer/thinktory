package com.mibe.thinktory.service.topic

import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.MongoRepository

interface TopicRepository : MongoRepository<Topic, ObjectId> {
    fun findByName(name: String): Topic?

//    fun findByNameLike(name: String, page: PageRequest): Page<Topic>
    fun findByNameLike(name: String): List<Topic>
}