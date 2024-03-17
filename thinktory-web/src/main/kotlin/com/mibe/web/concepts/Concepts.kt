package com.mibe.web.concepts

import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.concept.ConceptCreateCommand
import com.mibe.thinktory.service.concept.ConceptUpdateCommand
import com.mibe.web.models.ConceptCreateRequest
import com.mibe.web.models.ConceptModel
import com.mibe.web.models.ConceptUpdateRequest
import java.time.OffsetDateTime

fun ConceptCreateRequest.toCommand() = ConceptCreateCommand(
        title = title,
        theory = theory,
        labels = labels?.toSet() ?: emptySet()
    )

fun ConceptUpdateRequest.toCommand(id: Long) = ConceptUpdateCommand(
    id = id,
    title = title,
    theory = theory,
    labels = labels?.toSet() ?: emptySet()
)

fun Concept.toModel() = ConceptModel(
     id = id,
    title = title,
    theory = theory,
    questions = emptyList(),
    labels = labels.toList(),
    createdTime = OffsetDateTime.MIN,
    updatedTime = OffsetDateTime.MIN
)