package com.mibe.thinktory.service.book

import com.mibe.thinktory.service.concept.UnlabeledConcept
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookServiceImpl(
    private val bookRepository: BookRepository
) : BookService {

    override fun createBook(telegramId: Long): Book {
        return bookRepository.save(createEmptyBook(telegramId))
    }

    private fun createEmptyBook(userId: Long) = Book(telegramId = userId)

    override fun getBook(telegramId: Long): Book {
        return getBookOrNull(telegramId) ?: throw BookNotFoundException(telegramId)
    }

    override fun getBookOrNull(telegramId: Long): Book? {
        return bookRepository.findByTelegramId(telegramId)
    }

    override fun getOrCreateBook(telegramId: Long) = getBookOrNull(telegramId) ?: createBook(telegramId)

    override fun linkConcept(unlabeledConcept: UnlabeledConcept) {
//        getBook()
    }
}