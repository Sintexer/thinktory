package com.mibe.thinktory.service.concept

import com.mibe.thinktory.service.question.Question
import com.mibe.thinktory.service.topic.Topic
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "concepts")
data class Concept(
        @Id val id: ObjectId = ObjectId.get(),
        val title: String,
        val userId: Long,
        @DBRef val topic: Topic? = null,
        @TextIndexed val content: String? = null,
        val description: String? = null,
        val questions: List<Question> = emptyList(),
        val labels: Set<String> = emptySet(),
        val advance: ConceptLearnAdvance = ConceptLearnAdvance(),
        @LastModifiedDate val lastUpdate: LocalDateTime? = null
)