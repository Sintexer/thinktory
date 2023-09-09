package com.mibe.thinktory.service.book

import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.concept.UnlabeledConcept
import com.mibe.thinktory.service.topic.Topic
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "books")
data class Book(
    @Id val id: ObjectId = ObjectId.get(),
    val telegramId: Long,
    @DBRef val index: Map<Topic, List<Concept>> = emptyMap(),
    @DBRef val unlabeledConcepts: List<UnlabeledConcept> = emptyList()
)