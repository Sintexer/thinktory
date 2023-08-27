package thinktory.concept

import thinktory.question.Question
import thinktory.topic.Topic
import thinktory.user.User
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