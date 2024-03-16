package com.mibe.thinktory.service.concept

import dev.nesk.akkurate.annotations.Validate

@Validate
data class ConceptsQuery(
    val page: Int = 0,
    val pageSize: Int = 5,
    val substring: String = ""
)