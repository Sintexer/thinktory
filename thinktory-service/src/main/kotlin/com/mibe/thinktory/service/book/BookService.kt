package com.mibe.thinktory.service.book

interface BookService {
    fun createBook(userId: String): Book
    fun getBook(userId: String): Book
}