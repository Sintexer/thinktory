package com.mibe.thinktory.service.concept.exception

import com.mibe.thinktory.service.utils.ThinktoryException
import org.bson.types.ObjectId

class IllegalConceptPageException(
    val page: Int,
    message: String
) : ThinktoryException("Illegal page referenced: $page. Description: $message")