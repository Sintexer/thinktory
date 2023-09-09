package com.mibe.thinktory.service.book

import com.mibe.thinktory.service.concept.UnlabeledConcept

interface BookService {
    fun createBook(telegramId: Long): Book
    fun getBook(telegramId: Long): Book
    fun getBookOrNull(telegramId: Long): Book?
    fun getOrCreateBook(telegramId: Long): Book
    fun linkConcept(unlabeledConcept: UnlabeledConcept)
}