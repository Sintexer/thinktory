package com.mibe.thinktory.service.concept

import com.mibe.thinktory.service.topic.Topic
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "concepts")
data class UnlabeledConcept(
        @Id val id: ObjectId = ObjectId.get(),
        val content: String,
        val title: String? = null,
        val description: String? = null,
        val questions: List<Question> = emptyList(),
)