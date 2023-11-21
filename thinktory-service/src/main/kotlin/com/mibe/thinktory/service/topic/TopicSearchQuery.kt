package com.mibe.thinktory.service.topic

import kotlin.math.max

data class TopicSearchQuery(
    val page: Int = 0,
    val topicSubstring: String = ""
) {

    fun nextPage(): TopicSearchQuery {
        return this.copy(page = page + 1)
    }

    fun prevPage(): TopicSearchQuery {
        return this.copy(page = max(page -1, 0))
    }
}

fun TopicSearchQuery?.nextPage(): TopicSearchQuery {
    return this?.nextPage() ?: TopicSearchQuery()
}

fun TopicSearchQuery?.prevPage(): TopicSearchQuery {
    return this?.prevPage() ?: TopicSearchQuery()
}