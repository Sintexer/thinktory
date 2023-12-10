package com.mibe.thinktory.service.quiz

import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.concept.ConceptService
import com.mibe.thinktory.service.concept.ConceptsQuery
import org.springframework.stereotype.Service

@Service
class QuizServiceImpl(
    val conceptService: ConceptService
) : QuizService {

    override fun createQuiz(parameters: QuizParameters): Quiz {
        val concepts = getConcepts(parameters)
        val quizQuestions = concepts.map(::mapToQuizQuestion).toList()
        return Quiz(quizQuestions)
    }

    private fun getConcepts(parameters: QuizParameters): List<Concept> {
        val conceptsPage = conceptService.getPageOfLeastAnswered(parameters.userId, getConceptsQuery(parameters))
        return conceptsPage.toList().shuffled()
    }

    private fun getConceptsQuery(parameters: QuizParameters): ConceptsQuery {
        return ConceptsQuery(
            pageSize = parameters.type.numberOfQuestions,
            topicId = parameters.topicId
        )
    }

    private fun mapToQuizQuestion(concept: Concept): QuizQuestion {
        return QuizQuestion(getRandomConceptHint(concept), concept.id)
    }

    private fun getRandomConceptHint(concept: Concept): String {
        return if (concept.questions.isNotEmpty()) {
            concept.questions.random().content
        } else {
            concept.title
        }
    }

    override fun updateQuizOnFailure(quiz: Quiz): Quiz {
        TODO("Not yet implemented")
    }
}