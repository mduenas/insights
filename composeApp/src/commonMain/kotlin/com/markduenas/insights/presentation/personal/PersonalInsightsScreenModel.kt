package com.markduenas.insights.presentation.personal

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.markduenas.insights.billing.FREE_PERSONAL_INSIGHT_LIMIT
import com.markduenas.insights.data.PremiumRepository
import com.markduenas.insights.domain.model.Insight
import com.markduenas.insights.domain.repository.PersonalInsightRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PersonalInsightsState(
    val insights: List<Insight> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isPremium: Boolean = false,
    val showPaywall: Boolean = false,
)

class PersonalInsightsScreenModel(
    private val personalRepo: PersonalInsightRepository,
    private val premiumRepository: PremiumRepository,
) : ScreenModel {

    private val _state = MutableStateFlow(
        PersonalInsightsState(isPremium = premiumRepository.isPremium)
    )
    val state: StateFlow<PersonalInsightsState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            personalRepo.getPersonalInsights()
                .catch { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
                .collect { list -> _state.update { it.copy(insights = list, isLoading = false) } }
        }
        screenModelScope.launch {
            premiumRepository.premiumActive.collect { premium ->
                _state.update { it.copy(isPremium = premium) }
            }
        }
    }

    fun delete(id: String) {
        screenModelScope.launch {
            try { personalRepo.deletePersonalInsight(id) }
            catch (e: Exception) { _state.update { it.copy(error = e.message) } }
        }
    }

    fun onAddTapped(onNavigate: () -> Unit) {
        val s = _state.value
        if (!s.isPremium && s.insights.size >= FREE_PERSONAL_INSIGHT_LIMIT) {
            _state.update { it.copy(showPaywall = true) }
        } else {
            onNavigate()
        }
    }

    fun dismissPaywall() = _state.update { it.copy(showPaywall = false) }
}
