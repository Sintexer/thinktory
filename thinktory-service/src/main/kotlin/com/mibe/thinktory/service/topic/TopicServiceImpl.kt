package com.mibe.thinktory.service.topic

import org.springframework.stereotype.Service

@Service
class TopicServiceImpl(
        private val topicRepository: TopicRepository
) : TopicService {

    override fun getAllTopics(userId: String): List<Topic> {
        return topicRepository.findAll()
    }

    override fun getTopicById(userId: String, topicId: String): Topic {
        TODO("Not yet implemented")
    }

    override fun createTopic(userId: String, topic: Topic): Topic {
        TODO("Not yet implemented")
    }

    override fun updateTopic(userId: String, topicId: String, updatedTopic: Topic): Topic {
        TODO("Not yet implemented")
    }

    override fun deleteTopic(userId: String, topicId: String) {
        TODO("Not yet implemented")
    }
}