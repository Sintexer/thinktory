package com.mibe.thinktory.service.topic

import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface TopicRepository : MongoRepository<Topic, ObjectId> {

    fun findByName(name: String): Topic?

    @Query("{'name': {\$regex : ?0, \$options: 'i'}}")
    fun findByNameRegex(nameRegex: String, pageRequest: PageRequest): Page<Topic>
}