package com.mibe.thinktory.service.concept

data class ConceptLearnAdvance (
    var answered: Int = 0,
    var failed: Int = 0
) {

    fun advancePositively() {
        ++answered
    }

    fun advanceNegatively() {
        ++failed
    }
}