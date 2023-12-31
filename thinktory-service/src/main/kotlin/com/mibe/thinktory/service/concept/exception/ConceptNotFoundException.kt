package com.mibe.thinktory.service.concept.exception

import com.mibe.thinktory.service.utils.ThinktoryException
import org.bson.types.ObjectId

class ConceptNotFoundException(
    val id: ObjectId
) : ThinktoryException() {
}