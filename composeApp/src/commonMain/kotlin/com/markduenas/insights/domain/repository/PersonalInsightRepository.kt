package com.markduenas.insights.domain.repository

import com.markduenas.insights.domain.model.Insight
import kotlinx.coroutines.flow.Flow

interface PersonalInsightRepository {
    fun getPersonalInsights(): Flow<List<Insight>>

    fun getPersonalInsightById(id: String): Flow<Insight?>

    suspend fun countPersonalInsights(): Int

    suspend fun savePersonalInsight(insight: Insight)

    suspend fun updatePersonalInsight(insight: Insight)

    suspend fun deletePersonalInsight(id: String)

    /** Push any locally pending insights to Firestore (premium + signed-in). */
    suspend fun syncPendingInsights()

    /** Pull remote personal insights into local DB (premium + signed-in). */
    suspend fun pullRemotePersonalInsights(userId: String)

    /**
     * Deletes all personal insights for [userId] from Firestore and clears local
     * personal data + sync queue. Used for account deletion.
     */
    suspend fun deleteAllUserData(userId: String)
}
