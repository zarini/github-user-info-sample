package com.milkyhead.android.payconiq.presentation.event


data class SearchEvent(
    val query: String,
    val loadMore: Boolean = false,
    val retry: Boolean = false,
    val currentCount: Int = 0
)