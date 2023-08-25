package com.mibe.thinktory.concept

import com.mibe.thinktory.question.Question
import com.mibe.thinktory.topic.Topic
import com.mibe.thinktory.user.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="concepts")
data class Concept(
        val id: ObjectId = ObjectId.get(),
        val title: String?,
        val description: String?,
        val content: String?,
        val questions: List<Question>,
        @DBRef val topic: Topic,
        @DBRef val subTopic: Topic?,
        val label: List<String>,
        @DBRef val user: User
)