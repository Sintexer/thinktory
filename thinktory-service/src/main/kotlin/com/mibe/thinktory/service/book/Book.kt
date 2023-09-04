package com.mibe.thinktory.service.book

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "books")
data class Book(
    @Id val id: ObjectId = ObjectId.get(),
    val telegramId: Long,
    val index: Map<ObjectId, List<ObjectId>>
)