package com.markduenas.insights.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class PersonalInsightRepositoryImpl(
    private val database: InsightsDatabase,
    private val firestore: FirebaseFirestore
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

    override suspend fun savePersonalInsight(insight: Insight) = withContext(Dispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
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
                userId = insight.userId
            )
            queries.enqueue(
                id = insight.id,
                operation = "INSERT",
                userId = insight.userId ?: "",
                enqueuedAt = now
            )
        }
    }

    override suspend fun updatePersonalInsight(insight: Insight) = withContext(Dispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
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
            queries.enqueue(
                id = insight.id,
                operation = "UPDATE",
                userId = insight.userId ?: "",
                enqueuedAt = now
            )
        }
    }

    override suspend fun deletePersonalInsight(id: String) = withContext(Dispatchers.Default) {
        val row = queries.getById(id).executeAsOneOrNull()
        database.transaction {
            queries.delete(id)
            queries.enqueue(
                id = id,
                operation = "DELETE",
                userId = row?.userId ?: "",
                enqueuedAt = Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    override suspend fun syncPendingInsights() {
        val pending = withContext(Dispatchers.Default) {
            queries.getPendingSync().executeAsList()
        }
        for (item in pending) {
            try {
                syncItem(item)
                withContext(Dispatchers.Default) { queries.dequeue(item.id) }
            } catch (_: Exception) {
                // Leave in queue for next sync attempt
            }
        }
    }

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
