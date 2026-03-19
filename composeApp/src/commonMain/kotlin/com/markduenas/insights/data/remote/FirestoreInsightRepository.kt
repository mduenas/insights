package com.markduenas.insights.data.remote

import com.markduenas.insights.domain.model.Insight
import com.markduenas.insights.domain.model.InsightCategory
import com.markduenas.insights.domain.model.InsightStatus
import com.markduenas.insights.domain.model.Source
import com.markduenas.insights.domain.repository.InsightRepository
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

private const val COLLECTION_INSIGHTS = "insights"
private const val COLLECTION_PENDING = "pending_insights"

class FirestoreInsightRepository(
    private val firestore: FirebaseFirestore
) : InsightRepository {

    override fun getCommonInsights(limit: Int, afterId: String?): Flow<List<Insight>> =
        firestore.collection(COLLECTION_INSIGHTS)
            .orderBy("createdAt", Direction.DESCENDING)
            .limit(limit.toLong())
            .snapshots
            .map { snapshot -> snapshot.documents.mapNotNull { it.toInsight() } }

    override fun getInsightById(id: String): Flow<Insight?> =
        firestore.collection(COLLECTION_INSIGHTS)
            .document(id)
            .snapshots
            .map { it.toInsight() }

    override fun searchInsights(query: String): Flow<List<Insight>> {
        val lower = query.lowercase()
        return firestore.collection(COLLECTION_INSIGHTS)
            .orderBy("titleLower")
            .where { "titleLower" greaterThanOrEqualTo lower }
            .where { "titleLower" lessThan lower + "\uf8ff" }
            .snapshots
            .map { snapshot -> snapshot.documents.mapNotNull { it.toInsight() } }
    }

    override fun getPendingInsights(): Flow<List<Insight>> =
        firestore.collection(COLLECTION_PENDING)
            .orderBy("createdAt", Direction.DESCENDING)
            .snapshots
            .map { snapshot -> snapshot.documents.mapNotNull { it.toInsight() } }

    override suspend fun approveInsight(id: String) {
        val pendingRef = firestore.collection(COLLECTION_PENDING).document(id)
        val data = pendingRef.get().data<Map<String, Any>>()
        firestore.collection(COLLECTION_INSIGHTS).document(id).set(
            data + mapOf("status" to InsightStatus.APPROVED.name)
        )
        pendingRef.delete()
    }

    override suspend fun rejectInsight(id: String) {
        firestore.collection(COLLECTION_PENDING).document(id)
            .update("status" to InsightStatus.REJECTED.name)
    }
}

private fun dev.gitlive.firebase.firestore.DocumentSnapshot.toInsight(): Insight? = try {
    val data = data<Map<String, Any?>>()
    Insight(
        id = id,
        title = data["title"] as? String ?: return null,
        body = data["body"] as? String ?: return null,
        source = (data["source"] as? Map<*, *>)?.toSource() ?: return null,
        category = InsightCategory.valueOf(data["category"] as? String ?: "COMMON"),
        tags = (data["tags"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
        linkedCommonInsightId = data["linkedCommonInsightId"] as? String,
        status = InsightStatus.valueOf(data["status"] as? String ?: "APPROVED"),
        createdAt = Instant.fromEpochMilliseconds((data["createdAt"] as? Number)?.toLong() ?: 0L),
        updatedAt = Instant.fromEpochMilliseconds((data["updatedAt"] as? Number)?.toLong() ?: 0L),
        userId = data["userId"] as? String
    )
} catch (e: Exception) {
    null
}

private fun Map<*, *>.toSource() = Source(
    title = this["title"] as? String ?: "",
    author = this["author"] as? String,
    url = this["url"] as? String,
    year = (this["year"] as? Number)?.toInt()
)
