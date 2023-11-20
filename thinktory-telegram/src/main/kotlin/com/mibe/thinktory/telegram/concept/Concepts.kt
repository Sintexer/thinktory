package com.mibe.thinktory.telegram.concept

import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.concept.ConceptsQuery
import eu.vendeli.tgbot.types.EntityType
import eu.vendeli.tgbot.utils.builders.EntitiesContextBuilder

const val CONCEPT_PREVIEW_LENGTH = 15

fun getMarkdnownRender(concept: Concept) : EntitiesContextBuilder.() -> String = {
    "" - line(title(concept)) -
            line(topic(concept)) -
            "\n" -
            theory(concept)
}

private fun EntitiesContextBuilder.title(concept: Concept) =
    if (concept.title.isNullOrBlank()) {
        italic { "Untitled concept" }
    } else {
        bold { concept.title!! }
    }

private fun EntitiesContextBuilder.topic(concept: Concept) = bold { concept.getTopicOrEmpty() }

private fun theory(concept: Concept) = concept.content

private fun Concept.getTopicOrEmpty(): String = this.topic?.name ?: ""

private fun line(text: Pair<EntityType, String>) = if (text.second.isBlank()) {
    text
} else {
    Pair(text.first, text.second + "\n")
}

fun ConceptsQuery.toUrlParameters() = "?page=$page"

fun Concept.getShortPreviewString() = getPreviewString().shortened()
fun Concept.getPreviewString() = this.title ?: this.description ?: this.content

private fun String.shortened() = if (length <= CONCEPT_PREVIEW_LENGTH) this else this.substring(0..CONCEPT_PREVIEW_LENGTH - 3) + "..."