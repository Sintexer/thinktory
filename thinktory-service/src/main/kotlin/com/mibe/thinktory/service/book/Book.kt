package com.mibe.thinktory.service.book

import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.topic.Topic
import com.mibe.thinktory.service.user.User
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document("books")
data class Book (
        @Id val id: ObjectId = ObjectId.get(),
        val concepts: MutableMap<Topic, List<Concept>>,
        @DBRef val author: User
)