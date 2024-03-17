package com.mibe.thinktory.service.concept

data class ConceptCreateCommand(
    val title: String,
    val theory: String,
    val labels: Set<String>
)

fun ConceptCreateCommand.toConcept() = Concept(
    title = title,
    theory = theory,
    labels = labels.toSet()
)

data class ConceptUpdateCommand(
    val id: Long,
    val title: String,
    val theory: String,
    val labels: Set<String>
)

fun ConceptUpdateCommand.toConcept() = Concept(
    id = id,
    title = title,
    theory = theory,
    labels = labels.toSet()
)
