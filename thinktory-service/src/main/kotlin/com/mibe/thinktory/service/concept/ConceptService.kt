package com.mibe.thinktory.service.concept

import com.mibe.thinktory.service.NotFoundByIdException

interface ConceptService {
    fun createConcept(createCommand: ConceptCreateCommand): Concept
    fun getByIdOrNull(id: Long): Concept?
    fun updateConcept(updateCommand: ConceptUpdateCommand): Concept
    fun deleteById(id: Long)
}

class ConceptNotFoundException(id: Long): NotFoundByIdException(id)