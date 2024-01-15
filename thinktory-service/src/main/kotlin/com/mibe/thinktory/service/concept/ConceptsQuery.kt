package com.mibe.thinktory.service.concept

import dev.nesk.akkurate.annotations.Validate
import org.bson.types.ObjectId

@Validate
data class ConceptsQuery(
    val page: Int = 0,
    val pageSize: Int = 5,
    val substring: String = "",
    val topicId: ObjectId? = null
)