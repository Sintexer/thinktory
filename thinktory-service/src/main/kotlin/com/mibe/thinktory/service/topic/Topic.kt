package com.mibe.thinktory.service.topic

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="topics")
data class Topic(
        val id: ObjectId = ObjectId.get(),
        val name: String,
        @DBRef val parent: Topic? = null
)