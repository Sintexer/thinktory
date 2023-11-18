package com.mibe.thinktory.telegram.concept

import com.mibe.thinktory.service.concept.Concept
import eu.vendeli.tgbot.types.EntityType
import eu.vendeli.tgbot.utils.builders.EntitiesContextBuilder

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