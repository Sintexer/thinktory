package com.mibe.thinktory.service.quiz

import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.concept.ConceptService
import com.mibe.thinktory.service.question.Question
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageImpl

class QuizServiceImplTest : FeatureSpec({
    val conceptService = mockk<ConceptService>()
    val quizService = QuizServiceImpl(conceptService)

    val concept1 = conceptOf("Concept 1", "content 1", listOf("Q1", "Q2"))
    val concept2 = conceptOf("Concept 2", "content 2", listOf("Q3"))
    val concept3 = conceptOf("Concept 3", "content 3", listOf())
    val concept4 = conceptOf("Concept 4", "content 4", listOf("Q4", "Q5", "Q6", "Q7"))
    val concept5 = conceptOf("Concept 5", "content 5", listOf("Q8"))
    val concept6 = conceptOf("Concept 6", "content 6", listOf())
    val concept7 = conceptOf("Concept 7", "content 7", listOf())
    val concept8 = conceptOf("Concept 8", "content 8", listOf("Q9", "Q10", "Q11"))
    val concept9 = conceptOf("Concept 9", "content 9", listOf())
    val concept10 = conceptOf("Concept 10", "content 10", listOf("Q12", "Q13"))
    val concept11 = conceptOf("Concept 11", "content 11", listOf())
    val concepts = listOf(concept1, concept2, concept3, concept4, concept5, concept6, concept7,
        concept8, concept9, concept10, concept11)

    val conceptWithTitleOnly = conceptOf("Concept Title only", "content 11", listOf())
    val conceptWithTitleAndQuestion = conceptOf("Concept Title and a question", "content 11", listOf("Q"))

    feature("Quiz creation") {

        scenario("create quiz with correct number of questions") {
            withServiceReturnsQuestionsOfNumber(conceptService, concepts, Quiz.Type.SHORT.numberOfQuestions)
            val parameters = QuizParameters(userId = 1L, type = Quiz.Type.SHORT)
            val quiz = quizService.createQuiz(parameters)
            quiz.questions shouldHaveSize parameters.type.numberOfQuestions
        }
        scenario("quiz length is cut down if there are not enough concepts") {
            withServiceReturnsQuestionsOfNumber(conceptService, concepts, 2)
            val parameters = QuizParameters(userId = 1L, type = Quiz.Type.SHORT)
            val quiz = quizService.createQuiz(parameters)
            quiz.questions shouldHaveSize 2
        }
        scenario("returns empty list when no concepts found") {
            withServiceReturnsQuestionsOfNumber(conceptService, concepts, 0)
            val parameters = QuizParameters(userId = 1L, type = Quiz.Type.SHORT)
            val quiz = quizService.createQuiz(parameters)
            quiz.questions shouldHaveSize 0
        }
    }

    feature("Concept to quiz question mapping") {
        scenario("concept without question is hinted by its title") {
            withServiceReturnsQuestionsOfNumber(conceptService, listOf(conceptWithTitleOnly), 1)
            val parameters = QuizParameters(userId = 1L, type = Quiz.Type.SHORT)
            val quiz = quizService.createQuiz(parameters)
            quiz.questions shouldHaveSize 1
            quiz.getCurrentQuestion().question shouldBe "Concept Title only"
        }
        scenario("concept with at least one questions is referenced by question") {
            withServiceReturnsQuestionsOfNumber(conceptService, listOf(conceptWithTitleAndQuestion), 1)
            val parameters = QuizParameters(userId = 1L, type = Quiz.Type.SHORT)
            val quiz = quizService.createQuiz(parameters)
            quiz.questions shouldHaveSize 1
            quiz.getCurrentQuestion().question shouldBe "Q"
        }
    }
    
})

private fun withServiceReturnsQuestionsOfNumber(
    conceptService: ConceptService,
    concepts: List<Concept>,
    numberOfConcepts: Int
) {
    every { conceptService.getPageOfLeastAnswered(any(), any()) } returns
            PageImpl(concepts.subList(0, numberOfConcepts))
}

fun conceptOf(title: String, content: String, questions: List<String>, userId: Long = 1L): Concept {
    return Concept(
        title = title,
        userId = userId,
        content = content,
        questions = questions.map { Question(it) }
    )
}