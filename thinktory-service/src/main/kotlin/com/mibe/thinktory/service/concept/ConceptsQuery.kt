package com.mibe.thinktory.service.concept

import org.bson.types.ObjectId

data class ConceptsQuery(
    val page: Int = 0,
    val pageSize: Int = 5,
    val substring: String = "",
    val topicId: ObjectId? = null
)