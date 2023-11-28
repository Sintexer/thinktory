package com.mibe.thinktory.telegram.core.search

import com.mibe.thinktory.telegram.core.context.SearchContext
import eu.vendeli.tgbot.types.User

abstract class Search<Q, C : SearchContext> {

    protected abstract suspend fun startSearch(user: User)

    protected abstract fun getContext(userId: Long): C
    protected abstract fun updateContext(userId: Long, context: C?)
    protected abstract fun toQuery(context: C?): Q

}