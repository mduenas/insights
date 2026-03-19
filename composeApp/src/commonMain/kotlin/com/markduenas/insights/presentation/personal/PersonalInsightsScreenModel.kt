package com.markduenas.insights.presentation.personal

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.markduenas.insights.domain.model.Insight
import com.markduenas.insights.domain.repository.PersonalInsightRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PersonalInsightsState(
    val insights: List<Insight> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class PersonalInsightsScreenModel(
    private val personalRepo: PersonalInsightRepository
) : ScreenModel {

    private val _state = MutableStateFlow(PersonalInsightsState())
    val state: StateFlow<PersonalInsightsState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            personalRepo.getPersonalInsights()
                .catch { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
                .collect { list -> _state.update { it.copy(insights = list, isLoading = false) } }
        }
    }

    fun delete(id: String) {
        screenModelScope.launch {
            try { personalRepo.deletePersonalInsight(id) }
            catch (e: Exception) { _state.update { it.copy(error = e.message) } }
        }
    }
}
