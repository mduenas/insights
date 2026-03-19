package com.markduenas.insights.presentation.personal

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.markduenas.insights.currentTimeMillis
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
    val savedInsight: Insight? = null
)

class AddInsightScreenModel(
    private val personalRepo: PersonalInsightRepository,
    private val authRepository: AuthRepository,
    private val findMatches: FindMatchingInsightUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(AddInsightState())
    val state: StateFlow<AddInsightState> = _state.asStateFlow()

    fun onTitleChange(v: String) = _state.update { it.copy(title = v) }
    fun onBodyChange(v: String) = _state.update { it.copy(body = v) }
    fun onSourceTitleChange(v: String) = _state.update { it.copy(sourceTitle = v) }
    fun onSourceAuthorChange(v: String) = _state.update { it.copy(sourceAuthor = v) }
    fun onSourceUrlChange(v: String) = _state.update { it.copy(sourceUrl = v) }
    fun onSourceYearChange(v: String) = _state.update { it.copy(sourceYear = v) }
    fun onTagsChange(v: String) = _state.update { it.copy(tags = v) }

    fun save() {
        val s = _state.value
        if (s.title.isBlank() || s.body.isBlank() || s.sourceTitle.isBlank()) {
            _state.update { it.copy(error = "Title, body, and source title are required.") }
            return
        }
        screenModelScope.launch {
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
                val matches = findMatches(insight)
                if (matches.isNotEmpty()) {
                    _state.update {
                        it.copy(isSaving = false, matchSuggestions = matches,
                            showMatchDialog = true, savedInsight = insight)
                    }
                } else {
                    _state.update { it.copy(isSaving = false, savedInsight = insight) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    fun linkToCommonInsight(personalInsightId: String, commonInsightId: String) {
        screenModelScope.launch {
            val existing = _state.value.savedInsight ?: return@launch
            personalRepo.updatePersonalInsight(
                existing.copy(linkedCommonInsightId = commonInsightId)
            )
            _state.update { it.copy(showMatchDialog = false, matchSuggestions = emptyList()) }
        }
    }

    fun dismissMatchDialog() = _state.update { it.copy(showMatchDialog = false) }

    private fun generateId(): String =
        currentTimeMillis().toString(36) +
                (('a'..'z') + ('0'..'9')).shuffled().take(8).joinToString("")
}
