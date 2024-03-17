package com.mibe.web.concepts

import com.mibe.thinktory.service.concept.ConceptService
import com.mibe.web.apis.ConceptsApiDelegate
import com.mibe.web.models.ConceptCreateRequest
import com.mibe.web.models.ConceptModel
import com.mibe.web.models.ConceptUpdateRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.net.URI

@Service
class ConceptsAdapter(private val conceptsService: ConceptService) : ConceptsApiDelegate {

    override fun createConcept(conceptCreateRequest: ConceptCreateRequest): ResponseEntity<ConceptModel> {
        val concept = conceptsService.createConcept(conceptCreateRequest.toCommand())
        return ResponseEntity
            .created(URI.create("/concepts/${concept.id}"))
            .body(concept.toModel())
    }

    override fun getConceptByConceptId(id: Long): ResponseEntity<ConceptModel> {
        val concept = conceptsService.getByIdOrNull(id)
        return if (concept == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(concept.toModel())
        }
    }

    override fun updateConceptByConceptId(
        id: Long,
        conceptUpdateRequest: ConceptUpdateRequest
    ): ResponseEntity<ConceptModel> {
        val updatedConcept = conceptsService.updateConcept(conceptUpdateRequest.toCommand(id))
        return ResponseEntity.ok(updatedConcept.toModel())
    }
}