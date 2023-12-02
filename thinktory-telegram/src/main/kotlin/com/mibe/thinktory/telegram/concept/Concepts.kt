package com.mibe.thinktory.telegram.concept

import com.mibe.thinktory.service.concept.Concept
import eu.vendeli.tgbot.types.EntityType
import eu.vendeli.tgbot.utils.builders.EntitiesContextBuilder

fun getMarkdownRender(concept: Concept) : EntitiesContextBuilder.() -> String = {
    "" - line(title(concept)) -
            line(topic(concept)) -
            "\n" -
            theory(concept) -
            if (concept.questions.isNotEmpty()) { "\n" - concept.getQuestionsBlock() } else { "" }
}

private fun EntitiesContextBuilder.title(concept: Concept) = bold { concept.title }

private fun EntitiesContextBuilder.topic(concept: Concept) = bold { concept.getTopicOrEmpty() }

private fun theory(concept: Concept) = concept.content ?: "[empty body]"

private fun Concept.getTopicOrEmpty(): String = this.topic?.name?.let { "[$it]" } ?: ""

private fun line(text: Pair<EntityType, String>) = if (text.second.isBlank()) {
    text
} else {
    Pair(text.first, text.second + "\n")
}

fun Concept.getQuestionsBlock(): String {
    return this.questions.joinToString("\n") { "- $it" }
}