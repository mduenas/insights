package com.markduenas.insights.domain.repository

import com.markduenas.insights.domain.model.Insight
import kotlinx.coroutines.flow.Flow

interface PersonalInsightRepository {
    fun getPersonalInsights(): Flow<List<Insight>>

    fun getPersonalInsightById(id: String): Flow<Insight?>

    suspend fun savePersonalInsight(insight: Insight)

    suspend fun updatePersonalInsight(insight: Insight)

    suspend fun deletePersonalInsight(id: String)

    /** Push any locally pending insights to Firestore. */
    suspend fun syncPendingInsights()
}
