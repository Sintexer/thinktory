package com.mibe.thinktory.telegram.user

import com.mibe.thinktory.service.book.BookService
import com.mibe.thinktory.service.concept.Concept
import com.mibe.thinktory.service.concept.ConceptService
import eu.vendeli.tgbot.core.UserDataMapImpl
import eu.vendeli.tgbot.interfaces.UserData
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class UserDataServiceImpl(
    private val bookService: BookService,
    private val conceptService: ConceptService
) : UserDataService {

    private val userData: UserData = UserDataMapImpl

    override fun createConcept(telegramId: Long): Concept {
        TODO()
//
//        conceptService.createConcept(getBookId(telegramId), )
    }

    private fun getBookId(telegramId: Long): ObjectId {
        val bookId = userData.get<ObjectId>(telegramId, UserDataKeys.BOOK_ID)
        return if (bookId != null) {
            bookId
        } else {
            val book = bookService.getOrCreateBook(telegramId)
            userData.set(telegramId, UserDataKeys.BOOK_ID, book.id)
            book.id
        }
    }

}