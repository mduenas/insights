package com.markduenas.insights.presentation.admin

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.markduenas.insights.domain.model.Insight
import com.markduenas.insights.domain.repository.InsightRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AdminState(
    val pendingInsights: List<Insight> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class AdminScreenModel(
    private val insightRepository: InsightRepository
) : ScreenModel {

    private val _state = MutableStateFlow(AdminState())
    val state: StateFlow<AdminState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            insightRepository.getPendingInsights()
                .catch { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
                .collect { list -> _state.update { it.copy(pendingInsights = list, isLoading = false) } }
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
