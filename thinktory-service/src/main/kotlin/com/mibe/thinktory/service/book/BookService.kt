package com.mibe.thinktory.service.book

import com.mibe.thinktory.service.concept.Concept

interface BookService {
    fun createBook(userId: String): Book
    fun getBook(userId: String): Book
}