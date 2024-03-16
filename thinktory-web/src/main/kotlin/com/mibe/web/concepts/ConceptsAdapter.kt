package com.mibe.web.concepts

import com.mibe.thinktory.service.concept.ConceptService
import com.mibe.web.apis.ConceptsApiDelegate
import com.mibe.web.models.ConceptCreateRequest
import com.mibe.web.models.ConceptModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class ConceptsAdapter(private val conceptsService: ConceptService) : ConceptsApiDelegate {

    override fun createConcept(conceptCreateRequest: ConceptCreateRequest): ResponseEntity<ConceptModel> {
        val concept = conceptsService.createConcept(conceptCreateRequest.toCommand())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(concept.toModel())
    }

}