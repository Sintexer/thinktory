package com.mibe.thinktory.service.topic

interface TopicService {
    fun getAllTopics(userId: Long): List<Topic>
    fun getTopicById(userId: Long, topicId: String): Topic
    fun createTopic(userId: Long, topic: Topic): Topic
    fun getOrCreateTopicByName(userId: Long, topic: String): Topic
    fun updateTopic(userId: Long, topicId: String, updatedTopic: Topic): Topic
    fun deleteTopic(userId: Long, topicId: String)
}