package com.markduenas.insights.presentation.personal

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.markduenas.insights.billing.FREE_PERSONAL_INSIGHT_LIMIT
import com.markduenas.insights.currentTimeMillis
import com.markduenas.insights.data.PremiumRepository
import com.markduenas.insights.domain.CloudSyncCoordinator
import com.markduenas.insights.domain.model.Insight
import com.markduenas.insights.domain.model.InsightCategory
import com.markduenas.insights.domain.model.InsightStatus
import com.markduenas.insights.domain.model.Source
import com.markduenas.insights.domain.repository.AuthRepository
import com.markduenas.insights.domain.repository.PersonalInsightRepository
import com.markduenas.insights.domain.usecase.FindMatchingInsightUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

data class AddInsightState(
    val title: String = "",
    val body: String = "",
    val sourceTitle: String = "",
    val sourceAuthor: String = "",
    val sourceUrl: String = "",
    val sourceYear: String = "",
    val tags: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val matchSuggestions: List<Insight> = emptyList(),
    val showMatchDialog: Boolean = false,
    val savedInsight: Insight? = null,
    val showPaywall: Boolean = false,
    val personalCount: Int = 0,
    val isPremium: Boolean = false,
)

class AddInsightScreenModel(
    private val personalRepo: PersonalInsightRepository,
    private val authRepository: AuthRepository,
    private val findMatches: FindMatchingInsightUseCase,
    private val premiumRepository: PremiumRepository,
    private val cloudSyncCoordinator: CloudSyncCoordinator,
) : ScreenModel {

    private val _state = MutableStateFlow(AddInsightState())
    val state: StateFlow<AddInsightState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            val count = personalRepo.countPersonalInsights()
            _state.update {
                it.copy(personalCount = count, isPremium = premiumRepository.isPremium)
            }
        }
        screenModelScope.launch {
            premiumRepository.premiumActive.collect { premium ->
                _state.update { it.copy(isPremium = premium) }
            }
        }
    }

    fun onTitleChange(v: String) = _state.update { it.copy(title = v) }
    fun onBodyChange(v: String) = _state.update { it.copy(body = v) }
    fun onSourceTitleChange(v: String) = _state.update { it.copy(sourceTitle = v) }
    fun onSourceAuthorChange(v: String) = _state.update { it.copy(sourceAuthor = v) }
    fun onSourceUrlChange(v: String) = _state.update { it.copy(sourceUrl = v) }
    fun onSourceYearChange(v: String) = _state.update { it.copy(sourceYear = v) }
    fun onTagsChange(v: String) = _state.update { it.copy(tags = v) }
    fun dismissPaywall() = _state.update { it.copy(showPaywall = false) }
    fun dismissMatchDialog() = _state.update { it.copy(showMatchDialog = false) }

    fun linkToCommonInsight(personalInsightId: String, commonInsightId: String) {
        screenModelScope.launch {
            val existing = _state.value.savedInsight ?: return@launch
            personalRepo.updatePersonalInsight(
                existing.copy(linkedCommonInsightId = commonInsightId)
            )
            cloudSyncCoordinator.syncIfEligible()
            _state.update { it.copy(showMatchDialog = false, matchSuggestions = emptyList()) }
        }
    }

    fun save() {
        val s = _state.value
        if (s.title.isBlank() || s.body.isBlank() || s.sourceTitle.isBlank()) {
            _state.update { it.copy(error = "Title, body, and source title are required.") }
            return
        }
        screenModelScope.launch {
            val count = personalRepo.countPersonalInsights()
            val premium = premiumRepository.isPremium
            if (!premium && count >= FREE_PERSONAL_INSIGHT_LIMIT) {
                _state.update {
                    it.copy(
                        personalCount = count,
                        isPremium = false,
                        showPaywall = true,
                        error = "Free plan includes $FREE_PERSONAL_INSIGHT_LIMIT personal insights. Upgrade for unlimited.",
                    )
                }
                return@launch
            }

            _state.update { it.copy(isSaving = true, error = null) }
            val now = Instant.fromEpochMilliseconds(currentTimeMillis())
            val insight = Insight(
                id = generateId(),
                title = s.title.trim(),
                body = s.body.trim(),
                source = Source(
                    title = s.sourceTitle.trim(),
                    author = s.sourceAuthor.trim().takeIf { it.isNotBlank() },
                    url = s.sourceUrl.trim().takeIf { it.isNotBlank() },
                    year = s.sourceYear.trim().toIntOrNull()
                ),
                category = InsightCategory.PERSONAL,
                tags = s.tags.split(",").map { it.trim() }.filter { it.isNotBlank() },
                status = InsightStatus.APPROVED,
                createdAt = now,
                updatedAt = now,
                userId = authRepository.currentUserId
            )
            try {
                personalRepo.savePersonalInsight(insight)
                cloudSyncCoordinator.syncIfEligible()
                val matches = findMatches(insight)
                val newCount = personalRepo.countPersonalInsights()
                if (matches.isNotEmpty()) {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            matchSuggestions = matches,
                            showMatchDialog = true,
                            savedInsight = insight,
                            personalCount = newCount,
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            savedInsight = insight,
                            personalCount = newCount,
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    private fun generateId(): String =
        currentTimeMillis().toString(36) +
            (('a'..'z') + ('0'..'9')).shuffled().take(8).joinToString("")
}
