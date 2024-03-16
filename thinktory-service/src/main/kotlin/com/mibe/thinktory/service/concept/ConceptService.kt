package com.mibe.thinktory.service.concept

interface ConceptService {
    fun createConcept(createCommand: ConceptCreateCommand): Concept
}