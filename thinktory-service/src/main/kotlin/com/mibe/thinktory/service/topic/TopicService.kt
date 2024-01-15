package com.mibe.thinktory.service.topic

import org.bson.types.ObjectId
import org.springframework.data.domain.Page

interface TopicService {
    fun getAllTopics(userId: Long): List<Topic>
    fun getTopicById(userId: Long, topicId: ObjectId): Topic

    fun getOrCreateTopicByName(userId: Long, topic: String): Topic
    fun getTopicByName(userId: Long, topic: String): Topic

    fun getPage(userId: Long, topicSearchQuery: TopicSearchQuery): Page<Topic>
}