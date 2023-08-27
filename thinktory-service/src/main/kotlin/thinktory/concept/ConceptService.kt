package thinktory.concept

interface ConceptService {
    fun getAllConcepts(userId: String): List<Concept>
    fun getConceptById(userId: String, conceptId: String): Concept
    fun createConcept(userId: String, concept: Concept): Concept
    fun updateConcept(userId: String, conceptId: String, updatedConcept: Concept): Concept
    fun deleteConcept(userId: String, conceptId: String)
}