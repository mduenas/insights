package com.markduenas.insights.presentation.admin

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.markduenas.insights.domain.model.Insight
import com.markduenas.insights.domain.repository.InsightRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

data class TopicCount(val topic: String, val count: Int)

data class AdminState(
    val pendingInsights: List<Insight> = emptyList(),
    val topicRequests: List<TopicCount> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@Serializable
private data class TopicRequestDocument(
    val topic: String = "",
    val userId: String = "",
    val createdAt: Long = 0L
)

class AdminScreenModel(
    private val insightRepository: InsightRepository,
    private val firestore: FirebaseFirestore
) : ScreenModel {

    private val _state = MutableStateFlow(AdminState())
    val state: StateFlow<AdminState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            insightRepository.getPendingInsights()
                .catch { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
                .collect { list -> _state.update { it.copy(pendingInsights = list, isLoading = false) } }
        }
        loadTopicRequests()
    }

    private fun loadTopicRequests() {
        screenModelScope.launch {
            try {
                val snapshot = firestore.collection("topic_requests").get()
                val counts = mutableMapOf<String, Int>()
                snapshot.documents.forEach { doc ->
                    val topic = doc.data<TopicRequestDocument>().topic.trim().lowercase()
                    if (topic.isNotBlank()) counts[topic] = (counts[topic] ?: 0) + 1
                }
                val ranked = counts.entries
                    .sortedByDescending { it.value }
                    .map { TopicCount(it.key, it.value) }
                _state.update { it.copy(topicRequests = ranked) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun approve(id: String) {
        screenModelScope.launch {
            try { insightRepository.approveInsight(id) }
            catch (e: Exception) { _state.update { it.copy(error = e.message) } }
        }
    }

    fun reject(id: String) {
        screenModelScope.launch {
            try { insightRepository.rejectInsight(id) }
            catch (e: Exception) { _state.update { it.copy(error = e.message) } }
        }
    }
}
