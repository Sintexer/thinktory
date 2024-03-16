package com.mibe.thinktory.service.concept

data class ConceptCreateCommand(
    val title: String,
    val theory: String,
    val labels: List<String>
)
