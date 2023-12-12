package com.mibe.thinktory.telegram.concept

import com.mibe.thinktory.service.concept.Concept
import eu.vendeli.tgbot.types.EntityType
import eu.vendeli.tgbot.utils.builders.EntitiesContextBuilder

fun getMarkdownRender(concept: Concept): EntitiesContextBuilder.() -> String = {
    conceptWithoutQuestion(concept) -
            if (concept.questions.isNotEmpty()) {
                "\n\n" - concept.getQuestionsBlock()
            } else {
                ""
            }
}


fun getMarkdownRenderWithoutQuestions(concept: Concept): EntitiesContextBuilder.() -> String = {
    conceptWithoutQuestion(concept)
}

fun EntitiesContextBuilder.conceptWithoutQuestion(concept: Concept) = "" -
        line(title(concept)) -
        line(topic(concept)) -
        line(advance(concept)) -
        "\n" -
        theory(concept)


private fun EntitiesContextBuilder.title(concept: Concept) = bold { concept.title }

private fun EntitiesContextBuilder.topic(concept: Concept) = "Topic: " - bold { concept.getTopicOrEmpty() }

private fun advance(concept: Concept) = "Advance: answered ${concept.advance.answered} time(s)"

private fun theory(concept: Concept) = concept.content ?: "[empty body]"

private fun Concept.getTopicOrEmpty(): String = this.topic?.name ?: ""

private fun line(text: Pair<EntityType, String>) = if (text.second.isBlank()) {
    text
} else {
    Pair(text.first, text.second + "\n")
}

private fun line(text: String) = if (text.isBlank()) text else "$text\n"

fun Concept.getQuestionsBlock(): String {
    return "Questions:\n" + this.questions.joinToString("\n") { "- ${it.content}" }
}