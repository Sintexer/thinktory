package com.mibe.thinktory.service.topic

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class TopicServiceImpl(
        private val topicRepository: TopicRepository
) : TopicService {

    override fun getAllTopics(userId: Long): List<Topic> {
        return topicRepository.findAll()
    }

    override fun getTopicById(userId: Long, topicId: String): Topic {
        TODO("Not yet implemented")
    }

    override fun createTopic(userId: Long, topic: Topic): Topic {
        TODO("Not yet implemented")
    }

    override fun getOrCreateTopicByName(userId: Long, topic: String):Topic {
        return topicRepository.findByName(topic)?: topicRepository.save(Topic(name = topic))
    }

    override fun getTopicByName(userId: Long, topic: String): Topic {
        return topicRepository.findByName(topic)!!
    }

    override fun updateTopic(userId: Long, topicId: String, updatedTopic: Topic): Topic {
        TODO("Not yet implemented")
    }

    override fun deleteTopic(userId: Long, topicId: String) {
        TODO("Not yet implemented")
    }

    override fun getTopics(userId: Long, topicSearchQuery: TopicSearchQuery): Page<Topic> {
        val (topicSubstring, page) = topicSearchQuery
        return topicRepository.findByNameRegex(".*$topicSubstring.*", getTopicsPageRequest(page))
    }

    private fun getTopicsPageRequest(page: Int) = PageRequest.of(page, 5, Sort.by("name").ascending())
}