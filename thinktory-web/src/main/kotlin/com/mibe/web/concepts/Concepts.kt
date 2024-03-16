package com.mibe.web.concepts

import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.concept.ConceptCreateCommand
import com.mibe.web.models.ConceptCreateRequest
import com.mibe.web.models.ConceptModel
import java.time.OffsetDateTime

fun ConceptCreateRequest.toCommand() = ConceptCreateCommand(
        title = title,
        theory = theory,
        labels = labels ?: emptyList()
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