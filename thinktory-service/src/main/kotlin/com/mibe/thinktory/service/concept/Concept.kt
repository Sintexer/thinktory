package com.mibe.thinktory.service.concept

import com.mibe.thinktory.service.topic.Topic
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "concepts")
data class Concept(
        @Id val id: ObjectId = ObjectId.get(),
        val title: String?,
        val description: String?,
        val content: String,
        val questions: List<Question>,
        @DBRef val topic: Topic,
        val label: List<String>
)