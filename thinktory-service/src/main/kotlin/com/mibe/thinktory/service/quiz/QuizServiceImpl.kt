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

    override fun updateQuizOnSuccess(userId: Long, quiz: Quiz): Quiz {
        if (quiz.ended) {
            throw IllegalArgumentException("Cannot update empty or ended quiz")
        }
        val questions = quiz.questions.drop(1)
        return quiz.copy(questions = questions)
    }

    override fun updateQuizOnFailure(userId: Long, quiz: Quiz): Quiz {
        if (quiz.ended) {
            throw IllegalArgumentException("Cannot update empty or ended quiz")
        }
        val failedQuestion = quiz.questions.first()
        val remainingQuestions = quiz.questions.drop(1) + failedQuestion
        return quiz.copy(questions = remainingQuestions )
    }
}