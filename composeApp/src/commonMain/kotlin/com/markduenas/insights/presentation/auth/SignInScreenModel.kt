package com.markduenas.insights.presentation.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.markduenas.insights.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignInState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class SignInScreenModel(
    private val authRepository: AuthRepository
) : ScreenModel {

    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState> = _state.asStateFlow()

    fun onEmailChange(email: String) = _state.update { it.copy(email = email) }
    fun onPasswordChange(pw: String) = _state.update { it.copy(password = pw) }

    fun signIn(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.email.isBlank() || s.password.isBlank()) {
            _state.update { it.copy(error = "Email and password are required.") }
            return
        }
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.signInWithEmail(s.email, s.password)
                onSuccess()
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
