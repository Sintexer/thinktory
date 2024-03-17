package com.mibe.thinktory.service

open class NotFoundException() : RuntimeException()

open class NotFoundByIdException(val id: Long): NotFoundException()