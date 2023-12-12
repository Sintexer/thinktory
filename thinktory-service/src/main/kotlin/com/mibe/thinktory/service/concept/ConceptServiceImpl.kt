package com.mibe.thinktory.service.concept

import com.mibe.thinktory.service.concept.exception.ConceptNotFoundException
import com.mibe.thinktory.service.concept.exception.IllegalConceptPageException
import com.mibe.thinktory.service.question.Question
import com.mibe.thinktory.service.topic.TopicService
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Service
import java.util.regex.Pattern


@Service
class ConceptServiceImpl(
    private val conceptRepository: ConceptRepository,
    private val topicService: TopicService,
    private val mongoTemplate: MongoTemplate,
) : ConceptService {

    override fun createConceptFromTitle(userId: Long, title: String): Concept {
        val newConcept = Concept(title = title, userId = userId)
        return conceptRepository.save(newConcept)
    }

    override fun getLastEditedConcept(userId: Long): Concept? {
        return conceptRepository.findTopByUserIdOrderByLastUpdateDesc(userId)
    }

    override fun updateContent(conceptId: ObjectId, content: String): Concept {
        return conceptRepository.save(getById(conceptId).copy(content = content))
    }

    override fun updateTitle(conceptId: ObjectId, title: String): Concept {
        return conceptRepository.save(getById(conceptId).copy(title = title))
    }

    override fun updateTopic(conceptId: ObjectId, topicName: String): Concept {
        val concept = getById(conceptId)
        val topic = topicService.getOrCreateTopicByName(concept.userId, topicName)
        return conceptRepository.save(concept.copy(topic = topic))
    }

    override fun updateQuestions(conceptId: ObjectId, questions: List<Question>): Concept {
        return conceptRepository.save(getById(conceptId).copy(questions = questions))
    }

    override fun getById(conceptId: ObjectId): Concept {
        return conceptRepository.findById(conceptId).orElseThrow { ConceptNotFoundException(conceptId) }
    }

    override fun getPage(userId: Long, query: ConceptsQuery): Page<Concept> {
        verifyLowerPageBoundNotViolated(query)
        val conceptsPage = getPageByUserIdAndConceptsQuery(userId, query)
        verifyHigherPageBoundNotViolated(query, conceptsPage)
        return conceptsPage
    }

    private fun verifyLowerPageBoundNotViolated(query: ConceptsQuery) {
        if (query.page < 0) {
            throw IllegalConceptPageException(query.page, "Page number is negative")
        }
    }

    private fun getPageByUserIdAndConceptsQuery(userId: Long, query: ConceptsQuery): Page<Concept> {
        val (pageRequest, searchQuery) = buildConceptsQuery(query, userId)
        return getConceptsPage(searchQuery, pageRequest)
    }

    private fun verifyHigherPageBoundNotViolated(query: ConceptsQuery, conceptsPage: Page<Concept>) {
        if (query.page >= conceptsPage.totalPages && conceptsPage.totalPages != 0) {
            throw IllegalConceptPageException(query.page, "Page number exceeds max page: ${conceptsPage.totalPages}")
        }
    }

    override fun getPageOfLeastAnswered(userId: Long, query: ConceptsQuery): Page<Concept> {
        val (pageRequest, searchQuery) = buildConceptsQuery(query, userId)
        searchQuery.with(leastAnsweredConceptsSortOrder)
        return getConceptsPage(searchQuery, pageRequest)
    }

    private val leastAnsweredConceptsSortOrder = Sort.by("advance.answered").descending()

    private fun buildConceptsQuery(
        query: ConceptsQuery,
        userId: Long
    ): Pair<PageRequest, Query> {
        val pageRequest = PageRequest.of(query.page, query.pageSize)
        val searchQuery = Query(getConceptSubstringSearchCriteria(userId, query))
        return Pair(pageRequest, searchQuery)
    }

    private fun getConceptSubstringSearchCriteria(userId: Long, conceptsQuery: ConceptsQuery): Criteria {
        val criteria = where(Concept::userId).isEqualTo(userId)
        if (conceptsQuery.substring.isNotBlank()) {
            val pattern = mapToLowercaseSubstringPattern(conceptsQuery.substring)
            criteria.andOperator(where(Concept::title).regex(pattern, "i"))
        }

        return criteria
    }

    private fun mapToLowercaseSubstringPattern(substring: String) = ".*${Pattern.quote(substring)}.*"

    private fun getConceptsPage(
        searchQuery: Query,
        pageRequest: PageRequest
    ): PageImpl<Concept> {
        val count = mongoTemplate.count(searchQuery, Concept::class.java)
        val pagedSearchQuery = searchQuery.with(pageRequest)
        val conceptsList = mongoTemplate.find(pagedSearchQuery, Concept::class.java)

        return PageImpl(conceptsList, pageRequest, count)
    }

    override fun updatePositiveAdvance(userId: Long, conceptId: ObjectId) {
        val concept = getById(conceptId)
        concept.advance.advancePositively()
        conceptRepository.save(concept)
    }

    override fun updateNegativeAdvance(userId: Long, conceptId: ObjectId) {
        val concept = getById(conceptId)
        concept.advance.advanceNegatively()
        conceptRepository.save(concept)
    }
}