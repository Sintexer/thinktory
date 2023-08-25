package com.mibe.thinktory.topic

interface TopicService {
    fun getAllTopics(userId: String): List<Topic>
    fun getTopicById(userId: String, topicId: String): Topic
    fun createTopic(userId: String, topic: Topic): Topic
    fun updateTopic(userId: String, topicId: String, updatedTopic: Topic): Topic
    fun deleteTopic(userId: String, topicId: String)
}