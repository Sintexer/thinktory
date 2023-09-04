package com.mibe.thinktory.service.book

import com.mibe.thinktory.service.utils.ThinktoryException

class BookNotFoundException(val telegramId: Long): ThinktoryException()