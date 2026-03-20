package com.markduenas.insights.data.remote

import com.markduenas.insights.currentTimeMillis
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
import kotlinx.serialization.Serializable

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

    override fun filterByTag(tag: String): Flow<List<Insight>> =
        firestore.collection(COLLECTION_INSIGHTS)
            .where { "tags" contains tag }
            .orderBy("createdAt", Direction.DESCENDING)
            .snapshots
            .map { snapshot -> snapshot.documents.mapNotNull { it.toInsight() } }

    override fun getPendingInsights(): Flow<List<Insight>> =
        firestore.collection(COLLECTION_PENDING)
            .orderBy("createdAt", Direction.DESCENDING)
            .snapshots
            .map { snapshot -> snapshot.documents.mapNotNull { it.toInsight() } }

    override suspend fun approveInsight(id: String) {
        val pendingRef = firestore.collection(COLLECTION_PENDING).document(id)
        val doc = pendingRef.get().data<InsightDocument>()
        firestore.collection(COLLECTION_INSIGHTS).document(id).set(
            doc.copy(status = InsightStatus.APPROVED.name, updatedAt = currentTimeMillis())
        )
        pendingRef.delete()
    }

    override suspend fun rejectInsight(id: String) {
        firestore.collection(COLLECTION_PENDING).document(id)
            .update("status" to InsightStatus.REJECTED.name)
    }
}

private fun dev.gitlive.firebase.firestore.DocumentSnapshot.toInsight(): Insight? = try {
    val doc = data<InsightDocument>()
    Insight(
        id = id,
        title = doc.title.ifBlank { return null },
        body = doc.body.ifBlank { return null },
        source = Source(
            title = doc.source.title,
            author = doc.source.author,
            url = doc.source.url,
            year = doc.source.year
        ),
        category = runCatching { InsightCategory.valueOf(doc.category) }.getOrDefault(InsightCategory.COMMON),
        tags = doc.tags,
        linkedCommonInsightId = doc.linkedCommonInsightId,
        status = runCatching { InsightStatus.valueOf(doc.status) }.getOrDefault(InsightStatus.APPROVED),
        createdAt = Instant.fromEpochMilliseconds(doc.createdAt),
        updatedAt = Instant.fromEpochMilliseconds(doc.updatedAt),
        userId = doc.userId
    )
} catch (e: Exception) {
    null
}

@Serializable
private data class InsightDocument(
    val title: String = "",
    val body: String = "",
    val source: SourceDocument = SourceDocument(),
    val category: String = "COMMON",
    val tags: List<String> = emptyList(),
    val linkedCommonInsightId: String? = null,
    val status: String = "APPROVED",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val userId: String? = null
)

@Serializable
private data class SourceDocument(
    val title: String = "",
    val author: String? = null,
    val url: String? = null,
    val year: Int? = null
)
