package com.markduenas.insights.domain.model

data class Source(
    val title: String,
    val author: String? = null,
    val url: String? = null,
    val year: Int? = null
)
