package com.mibe.thinktory.service.concept

import com.mibe.thinktory.service.topic.Topic

data class ConceptsQuery(
    val page: Int = 0,
    val substring: String = "",
    val topic: Topic? = null
)