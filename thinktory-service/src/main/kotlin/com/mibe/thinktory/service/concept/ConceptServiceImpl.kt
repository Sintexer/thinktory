package com.mibe.thinktory.service.concept

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service


@Service
class ConceptServiceImpl(
    private val conceptRepository: ConceptRepository,
    private val conceptValidator: ConceptValidator
) : ConceptService {

    override fun createConcept(createCommand: ConceptCreateCommand): Concept {
        val concept = mapToConcept(createCommand)
        conceptValidator.validateConcept(concept)
        return conceptRepository.save(concept)
    }

    override fun getByIdOrNull(id: Long): Concept? {
        return conceptRepository.findByIdOrNull(id)
    }

    override fun updateConcept(updateCommand: ConceptUpdateCommand): Concept {
        if (!conceptRepository.existsById(updateCommand.id)) {
            throw ConceptNotFoundException(updateCommand.id)
        }

        val updatedConcept = updateCommand.toConcept()
        conceptValidator.validateConcept(updatedConcept)

        return conceptRepository.save(updatedConcept)
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