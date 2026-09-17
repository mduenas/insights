package com.markduenas.insights.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.markduenas.insights.currentTimeMillis
import com.markduenas.insights.data.local.db.InsightsDatabase
import com.markduenas.insights.data.local.db.PersonalInsight
import com.markduenas.insights.data.local.db.SyncQueue
import com.markduenas.insights.domain.model.Insight
import com.markduenas.insights.domain.model.InsightCategory
import com.markduenas.insights.domain.model.InsightStatus
import com.markduenas.insights.domain.model.Source
import com.markduenas.insights.domain.repository.PersonalInsightRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

class PersonalInsightRepositoryImpl(
    private val database: InsightsDatabase,
    private val firestore: FirebaseFirestore,
    /** Premium + signed-in cloud backup enabled. */
    private val isCloudSyncEnabled: () -> Boolean,
) : PersonalInsightRepository {

    private val queries get() = database.insightsDatabaseQueries

    override fun getPersonalInsights(): Flow<List<Insight>> =
        queries.getAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toDomain() } }

    override fun getPersonalInsightById(id: String): Flow<Insight?> =
        queries.getById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toDomain() }

    override suspend fun countPersonalInsights(): Int = withContext(Dispatchers.Default) {
        queries.countAll().executeAsOne().toInt()
    }

    override suspend fun savePersonalInsight(insight: Insight) = withContext(Dispatchers.Default) {
        val now = currentTimeMillis()
        val userId = insight.userId
        database.transaction {
            queries.insert(
                id = insight.id,
                title = insight.title,
                body = insight.body,
                sourceTitle = insight.source.title,
                sourceAuthor = insight.source.author,
                sourceUrl = insight.source.url,
                sourceYear = insight.source.year?.toLong(),
                tags = insight.tags.joinToString(","),
                linkedCommonInsightId = insight.linkedCommonInsightId,
                status = insight.status.name,
                createdAt = insight.createdAt.toEpochMilliseconds(),
                updatedAt = insight.updatedAt.toEpochMilliseconds(),
                userId = userId
            )
            if (shouldEnqueue(userId)) {
                queries.enqueue(
                    id = insight.id,
                    operation = "INSERT",
                    userId = userId ?: "",
                    enqueuedAt = now
                )
            }
        }
    }

    override suspend fun updatePersonalInsight(insight: Insight) = withContext(Dispatchers.Default) {
        val now = currentTimeMillis()
        val userId = insight.userId
        database.transaction {
            queries.update(
                title = insight.title,
                body = insight.body,
                sourceTitle = insight.source.title,
                sourceAuthor = insight.source.author,
                sourceUrl = insight.source.url,
                sourceYear = insight.source.year?.toLong(),
                tags = insight.tags.joinToString(","),
                linkedCommonInsightId = insight.linkedCommonInsightId,
                status = insight.status.name,
                updatedAt = now,
                id = insight.id
            )
            if (shouldEnqueue(userId)) {
                queries.enqueue(
                    id = insight.id,
                    operation = "UPDATE",
                    userId = userId ?: "",
                    enqueuedAt = now
                )
            }
        }
    }

    override suspend fun deletePersonalInsight(id: String) = withContext(Dispatchers.Default) {
        val row = queries.getById(id).executeAsOneOrNull()
        val userId = row?.userId
        database.transaction {
            queries.delete(id)
            if (shouldEnqueue(userId)) {
                queries.enqueue(
                    id = id,
                    operation = "DELETE",
                    userId = userId ?: "",
                    enqueuedAt = currentTimeMillis()
                )
            }
        }
    }

    override suspend fun syncPendingInsights() {
        if (!isCloudSyncEnabled()) return
        val pending = withContext(Dispatchers.Default) {
            queries.getPendingSync().executeAsList()
        }
        for (item in pending) {
            try {
                syncItem(item)
                withContext(Dispatchers.Default) { queries.dequeue(item.id) }
            } catch (_: Exception) {
                // Leave in queue for next attempt
            }
        }
    }

    override suspend fun pullRemotePersonalInsights(userId: String) {
        if (!isCloudSyncEnabled() || userId.isBlank()) return
        val snapshot = firestore
            .collection("users").document(userId)
            .collection("personal_insights")
            .get()

        for (doc in snapshot.documents) {
            try {
                val remote = doc.data<PersonalInsightDocument>()
                val remoteUpdated = remote.updatedAt
                val local = withContext(Dispatchers.Default) {
                    queries.getById(remote.id).executeAsOneOrNull()
                }
                if (local != null && local.updatedAt >= remoteUpdated) continue

                withContext(Dispatchers.Default) {
                    queries.insert(
                        id = remote.id,
                        title = remote.title,
                        body = remote.body,
                        sourceTitle = remote.source.title,
                        sourceAuthor = remote.source.author,
                        sourceUrl = remote.source.url,
                        sourceYear = remote.source.year?.toLong(),
                        tags = remote.tags.joinToString(","),
                        linkedCommonInsightId = remote.linkedCommonInsightId,
                        status = remote.status,
                        createdAt = remote.createdAt,
                        updatedAt = remote.updatedAt,
                        userId = remote.userId ?: userId
                    )
                }
            } catch (_: Exception) {
                // Skip malformed docs
            }
        }
    }

    private fun shouldEnqueue(userId: String?): Boolean =
        isCloudSyncEnabled() && !userId.isNullOrBlank()

    private suspend fun syncItem(item: SyncQueue) {
        if (item.userId.isBlank()) return
        val docRef = firestore
            .collection("users").document(item.userId)
            .collection("personal_insights").document(item.id)
        when (item.operation) {
            "INSERT", "UPDATE" -> {
                val row = withContext(Dispatchers.Default) {
                    queries.getById(item.id).executeAsOneOrNull()
                } ?: return
                docRef.set(row.toFirestoreMap())
            }
            "DELETE" -> docRef.delete()
        }
    }

    override suspend fun deleteAllUserData(userId: String) {
        val collection = firestore
            .collection("users").document(userId)
            .collection("personal_insights")
        val snapshot = collection.get()
        snapshot.documents.forEach { doc ->
            doc.reference.delete()
        }

        withContext(Dispatchers.Default) {
            database.transaction {
                queries.deleteAllPersonalInsights()
                queries.clearSyncQueue()
            }
        }
    }
}

// ── Mappers ─────────────────────────────────────────────────────────────────

private fun PersonalInsight.toDomain() = Insight(
    id = id,
    title = title,
    body = body,
    source = Source(
        title = sourceTitle,
        author = sourceAuthor,
        url = sourceUrl,
        year = sourceYear?.toInt()
    ),
    category = InsightCategory.PERSONAL,
    tags = if (tags.isBlank()) emptyList() else tags.split(","),
    linkedCommonInsightId = linkedCommonInsightId,
    status = InsightStatus.valueOf(status),
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = Instant.fromEpochMilliseconds(updatedAt),
    userId = userId
)

private fun PersonalInsight.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "title" to title,
    "body" to body,
    "source" to mapOf(
        "title" to sourceTitle,
        "author" to sourceAuthor,
        "url" to sourceUrl,
        "year" to sourceYear
    ),
    "category" to InsightCategory.PERSONAL.name,
    "tags" to if (tags.isBlank()) emptyList<String>() else tags.split(","),
    "linkedCommonInsightId" to linkedCommonInsightId,
    "status" to status,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt,
    "userId" to userId
)

@Serializable
private data class PersonalInsightDocument(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val source: SourceDocument = SourceDocument(),
    val tags: List<String> = emptyList(),
    val linkedCommonInsightId: String? = null,
    val status: String = InsightStatus.APPROVED.name,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val userId: String? = null,
)

@Serializable
private data class SourceDocument(
    val title: String = "",
    val author: String? = null,
    val url: String? = null,
    val year: Int? = null,
)
