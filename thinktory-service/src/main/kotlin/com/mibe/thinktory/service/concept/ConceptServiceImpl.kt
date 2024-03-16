package com.mibe.thinktory.service.concept

import org.springframework.stereotype.Service


@Service
class ConceptServiceImpl(
    private val conceptRepository: ConceptRepository,
    private val conceptValidator: ConceptValidator
) : ConceptService {

    override fun createConcept(createCommand: ConceptCreateCommand): Concept {
        val concept = mapToConcept(createCommand)
        conceptValidator.validateConcept(concept)
//        return Concept(0, "", "", emptySet())
        return conceptRepository.save(concept)
    }

    private fun mapToConcept(createCommand: ConceptCreateCommand): Concept {
        return createCommand.let {
            Concept(
                title = it.title,
                theory = it.theory,
                labels = it.labels.toSet()
            )
        }
    }
}