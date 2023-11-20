package com.mibe.thinktory.service.concept

import com.mibe.thinktory.service.concept.exception.ConceptNotFoundException
import com.mibe.thinktory.service.concept.exception.IllegalConceptPageException
import com.mibe.thinktory.service.topic.TopicService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service


@Service
class ConceptServiceImpl(
    private val conceptRepository: ConceptRepository,
    private val topicService: TopicService,
    @Value("\${thinktory.concepts.pageSize:5}")
    private val pageSize: Int
) : ConceptService {

    override fun createConcept(bookId: ObjectId, concept: Concept): Concept {
        TODO("Not yet implemented")
    }

    override fun createConceptFromTheory(userId: Long, theory: String): Concept {
        val newConcept = Concept(content = theory, userId = userId)
        return conceptRepository.save(newConcept)
    }

    override fun getRecentUserConcept(userId: Long): Concept? {
        return conceptRepository.findTopByUserIdOrderByLastUpdateDesc(userId)
    }

    override fun updateTitle(conceptId: ObjectId, title: String): Concept {
        val concept = conceptRepository.findById(conceptId).orElseThrow { ConceptNotFoundException(conceptId) }
        return conceptRepository.save(concept.copy(title = title))
    }

    override fun updateTopic(conceptId: ObjectId, topicName: String): Concept {
        val concept = conceptRepository.findById(conceptId).orElseThrow { ConceptNotFoundException(conceptId) }
        val topic = topicService.getOrCreateTopicByName(concept.userId, topicName)
        return conceptRepository.save(concept.copy(topic = topic))
    }

    override fun getById(conceptId: ObjectId): Concept {
        return conceptRepository.findById(conceptId).get()
    }

    private val conceptSortOrder = Sort.by("title").descending()
        .and(Sort.by("lastUpdate").descending())

    override fun getAll(userId: Long, query: ConceptsQuery): Page<Concept> {
        if (query.page < 0) {
            throw IllegalConceptPageException(query.page, "Page number is negative")
        }
        val pageRequest = PageRequest.of(query.page, pageSize)
        val conceptsPage = conceptRepository.findAllByUserId(userId, pageRequest)
        if (query.page >= conceptsPage.totalPages) {
            throw IllegalConceptPageException(query.page, "Page number exceeds max page: ${conceptsPage.totalPages}")
        }

        return conceptsPage
    }
}