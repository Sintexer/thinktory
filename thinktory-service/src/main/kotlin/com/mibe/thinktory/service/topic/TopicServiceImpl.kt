package com.mibe.thinktory.service.topic

import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class TopicServiceImpl(
        private val topicRepository: TopicRepository
) : TopicService {

    override fun getAllTopics(userId: Long): List<Topic> {
        return topicRepository.findAll()
    }

    override fun getTopicById(userId: Long, topicId: ObjectId): Topic {
        return topicRepository.findById(topicId).get()
    }

    override fun getOrCreateTopicByName(userId: Long, topic: String):Topic {
        return topicRepository.findByName(topic)?: topicRepository.save(Topic(name = topic))
    }

    override fun getTopicByName(userId: Long, topic: String): Topic {
        return topicRepository.findByName(topic)!!
    }

    override fun getPage(userId: Long, topicSearchQuery: TopicSearchQuery): Page<Topic> {
        val (page, topicSubstring) = topicSearchQuery
        return topicRepository.findByNameRegex(getSearchPattern(topicSubstring), getTopicsPageRequest(page))
    }

    private fun getSearchPattern(topicSubstring: String) = ".*${Pattern.quote(topicSubstring)}.*"

    private fun getTopicsPageRequest(page: Int) = PageRequest.of(page, 5, Sort.by("name").ascending())
}