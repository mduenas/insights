package com.markduenas.insights.presentation.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.markduenas.insights.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class SignUpScreenModel(
    private val authRepository: AuthRepository
) : ScreenModel {

    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state.asStateFlow()

    fun onEmailChange(email: String) = _state.update { it.copy(email = email, error = null) }
    fun onPasswordChange(pw: String) = _state.update { it.copy(password = pw, error = null) }
    fun onConfirmPasswordChange(pw: String) = _state.update { it.copy(confirmPassword = pw, error = null) }

    fun register(onSuccess: () -> Unit) {
        val s = _state.value
        when {
            s.email.isBlank() || s.password.isBlank() ->
                _state.update { it.copy(error = "Email and password are required.") }
            s.password != s.confirmPassword ->
                _state.update { it.copy(error = "Passwords do not match.") }
            s.password.length < 6 ->
                _state.update { it.copy(error = "Password must be at least 6 characters.") }
            else -> screenModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                try {
                    authRepository.registerWithEmail(s.email, s.password)
                    onSuccess()
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }
}
