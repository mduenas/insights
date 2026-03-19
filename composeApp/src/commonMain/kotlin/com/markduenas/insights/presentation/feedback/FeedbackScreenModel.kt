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

data class FeedbackState(
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

    fun onMessageChange(text: String) = _state.update { it.copy(message = text, error = null) }

    fun submit() {
        val message = _state.value.message.trim()
        if (message.isBlank()) {
            _state.update { it.copy(error = "Please enter a message before submitting.") }
            return
        }
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                firestore.collection("feedback").add(
                    mapOf(
                        "message" to message,
                        "userId" to (authRepository.currentUserId ?: "anonymous"),
                        "createdAt" to currentTimeMillis()
                    )
                )
                _state.update { it.copy(isLoading = false, isSubmitted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
