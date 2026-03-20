package com.markduenas.insights.presentation.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.markduenas.insights.domain.model.Insight
import com.markduenas.insights.domain.repository.InsightRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeState(
    val insights: List<Insight> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val activeTag: String? = null,
    val error: String? = null
)

class HomeScreenModel(
    private val insightRepository: InsightRepository
) : ScreenModel {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    private var collectJob: Job? = null

    init { observe(insightRepository.getCommonInsights()) }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query, activeTag = null) }
        val source = if (query.isBlank())
            insightRepository.getCommonInsights()
        else
            insightRepository.searchInsights(query)
        observe(source)
    }

    fun onTagSelected(tag: String) {
        val current = _state.value.activeTag
        if (current == tag) {
            // Tap same tag again → clear filter
            _state.update { it.copy(activeTag = null, searchQuery = "") }
            observe(insightRepository.getCommonInsights())
        } else {
            _state.update { it.copy(activeTag = tag, searchQuery = "") }
            observe(insightRepository.filterByTag(tag))
        }
    }

    private fun observe(source: Flow<List<Insight>>) {
        collectJob?.cancel()
        collectJob = screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            source
                .catch { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
                .collect { insights -> _state.update { it.copy(insights = insights, isLoading = false) } }
        }
    }
}
