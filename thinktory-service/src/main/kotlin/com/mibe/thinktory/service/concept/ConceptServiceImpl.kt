package com.mibe.thinktory.service.concept

import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class ConceptServiceImpl(
    private val conceptRepository: ConceptRepository
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
}