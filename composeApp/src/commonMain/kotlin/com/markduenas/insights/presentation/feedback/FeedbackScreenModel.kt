package com.markduenas.insights.presentation.feedback

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.markduenas.insights.currentTimeMillis
import com.markduenas.insights.domain.repository.AuthRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FeedbackMode { FEEDBACK, TOPIC }

data class FeedbackState(
    val mode: FeedbackMode = FeedbackMode.FEEDBACK,
    val message: String = "",
    val isLoading: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: String? = null
)

class FeedbackScreenModel(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) : ScreenModel {

    private val _state = MutableStateFlow(FeedbackState())
    val state: StateFlow<FeedbackState> = _state.asStateFlow()

    fun onModeChange(mode: FeedbackMode) =
        _state.update { it.copy(mode = mode, message = "", error = null) }

    fun onMessageChange(text: String) = _state.update { it.copy(message = text, error = null) }

    fun submit() {
        val message = _state.value.message.trim()
        if (message.isBlank()) {
            _state.update { it.copy(error = "Please enter a message before submitting.") }
            return
        }
        val mode = _state.value.mode
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val userId = authRepository.currentUserId ?: "anonymous"
                if (mode == FeedbackMode.FEEDBACK) {
                    firestore.collection("feedback").add(
                        mapOf(
                            "message" to message,
                            "userId" to userId,
                            "createdAt" to currentTimeMillis()
                        )
                    )
                } else {
                    firestore.collection("topic_requests").add(
                        mapOf(
                            "topic" to message,
                            "userId" to userId,
                            "createdAt" to currentTimeMillis()
                        )
                    )
                }
                _state.update { it.copy(isLoading = false, isSubmitted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
