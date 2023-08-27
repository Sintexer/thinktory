package thinktory.concept

data class ConceptQuestion (
        val title: String,
        val description: String?,
        val answer: String?,
        val labels: List<String>
)