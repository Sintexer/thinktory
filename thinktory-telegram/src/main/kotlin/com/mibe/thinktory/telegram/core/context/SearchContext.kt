package com.mibe.thinktory.telegram.core.context

interface SearchContext

interface PageableSearchContext : SearchContext {
    val page: Int
}

interface SubstringSearchContext : SearchContext {
    val searchSubstring: String
}

data class PagedSubstringSearchContext(
    override val page: Int = 0,
    override val searchSubstring: String = ""
) : SubstringSearchContext, PageableSearchContext