package com.markduenas.insights.domain.model

import kotlinx.datetime.Instant

data class Insight(
    val id: String,
    val title: String,
    val body: String,
    val source: Source,
    val category: InsightCategory,
    val tags: List<String> = emptyList(),
    /** Set on personal insights that have been matched to a common insight. */
    val linkedCommonInsightId: String? = null,
    val status: InsightStatus = InsightStatus.APPROVED,
    val createdAt: Instant,
    val updatedAt: Instant,
    /** Null for common insights; the owning user UID for personal insights. */
    val userId: String? = null
)
