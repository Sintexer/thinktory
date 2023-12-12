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
        requireQuizNotEnded(quiz)
        val answeredQuestion = quiz.questions.first()
        updateAnsweredConceptAdvance(userId, answeredQuestion)
        val remainingQuestions = quiz.questions.drop(1)
        return quiz.copy(questions = remainingQuestions)
    }

    private fun updateAnsweredConceptAdvance(userId: Long, answeredQuestion: QuizQuestion) {
        conceptService.updatePositiveAdvance(userId, answeredQuestion.conceptId)
    }

    override fun updateQuizOnFailure(userId: Long, quiz: Quiz): Quiz {
        requireQuizNotEnded(quiz)
        val failedQuestion = quiz.questions.first()
        updateFailedConceptAdvance(userId, failedQuestion)
        val reorderedQuestions = moveCurrentQuestionToTheEnd(quiz)
        return quiz.copy(questions = reorderedQuestions)
    }

    private fun moveCurrentQuestionToTheEnd(quiz: Quiz): List<QuizQuestion> {
        val first = quiz.questions.first()
        return quiz.questions.drop(1) + first
    }

    private fun updateFailedConceptAdvance(userId: Long, failedQuestion: QuizQuestion) {
        conceptService.updateNegativeAdvance(userId, failedQuestion.conceptId)
    }

    private fun requireQuizNotEnded(quiz: Quiz) {
        require(!quiz.ended) { "Cannot update empty or ended quiz" }
    }
}