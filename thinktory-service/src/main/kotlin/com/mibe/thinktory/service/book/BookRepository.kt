package com.mibe.thinktory.service.book

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface BookRepository : MongoRepository<Book, ObjectId> {
    fun findByTelegramId(telegramId: Long): Book?
}