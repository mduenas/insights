package com.markduenas.insights.domain.repository

import com.markduenas.insights.domain.model.Insight
import kotlinx.coroutines.flow.Flow

interface InsightRepository {
    /** Paginated stream of approved common insights. Pass [afterId] for the next page. */
    fun getCommonInsights(limit: Int = 20, afterId: String? = null): Flow<List<Insight>>

    fun getInsightById(id: String): Flow<Insight?>

    fun searchInsights(query: String): Flow<List<Insight>>

    fun filterByTag(tag: String): Flow<List<Insight>>

    /** Admin: stream of insights awaiting approval. */
    fun getPendingInsights(): Flow<List<Insight>>

    suspend fun approveInsight(id: String)

    suspend fun rejectInsight(id: String)
}
