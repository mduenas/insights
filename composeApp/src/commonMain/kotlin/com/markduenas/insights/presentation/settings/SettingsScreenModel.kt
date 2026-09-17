package com.markduenas.insights.presentation.settings

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.markduenas.insights.billing.FREE_PERSONAL_INSIGHT_LIMIT
import com.markduenas.insights.data.BillingRepository
import com.markduenas.insights.data.PremiumRepository
import com.markduenas.insights.domain.CloudSyncCoordinator
import com.markduenas.insights.domain.repository.AuthRepository
import com.markduenas.insights.domain.repository.PersonalInsightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsState(
    val isSignedIn: Boolean = false,
    val email: String? = null,
    val isBusy: Boolean = false,
    val error: String? = null,
    val didLeaveAccount: Boolean = false,
    val isPremium: Boolean = false,
    val personalCount: Int = 0,
    val showPaywall: Boolean = false,
    val restoreInProgress: Boolean = false,
    val exportMessage: String? = null,
    val showDebugPremiumToggle: Boolean = false,
    val debugPremiumOverride: Boolean = false,
)

class SettingsScreenModel(
    private val authRepository: AuthRepository,
    private val personalInsightRepository: PersonalInsightRepository,
    private val premiumRepository: PremiumRepository,
    private val billingRepository: BillingRepository,
    private val cloudSyncCoordinator: CloudSyncCoordinator,
) : ScreenModel {

    private val _state = MutableStateFlow(
        SettingsState(
            isSignedIn = authRepository.currentUserId != null,
            email = authRepository.currentUserEmail,
            isPremium = premiumRepository.isPremium,
            showDebugPremiumToggle = premiumRepository.showDebugPremiumToggle,
            debugPremiumOverride = premiumRepository.isDebugPremiumOverride,
        )
    )
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            combine(
                authRepository.isSignedIn,
                premiumRepository.premiumActive,
            ) { signedIn, premium ->
                signedIn to premium
            }.collect { (signedIn, premium) ->
                val count = runCatching {
                    personalInsightRepository.countPersonalInsights()
                }.getOrDefault(0)
                _state.update {
                    it.copy(
                        isSignedIn = signedIn,
                        email = authRepository.currentUserEmail,
                        isPremium = premium,
                        personalCount = count,
                        showDebugPremiumToggle = premiumRepository.showDebugPremiumToggle,
                        debugPremiumOverride = premiumRepository.isDebugPremiumOverride,
                    )
                }
                if (premium && signedIn) {
                    cloudSyncCoordinator.syncIfEligible()
                }
            }
        }
        screenModelScope.launch {
            val count = personalInsightRepository.countPersonalInsights()
            _state.update { it.copy(personalCount = count) }
        }
    }

    fun clearError() = _state.update { it.copy(error = null, exportMessage = null) }

    fun showPaywall() = _state.update { it.copy(showPaywall = true, error = null) }
    fun dismissPaywall() = _state.update { it.copy(showPaywall = false) }

    fun setDebugPremiumOverride(enabled: Boolean) {
        premiumRepository.setDebugPremiumOverride(enabled)
        _state.update {
            it.copy(
                debugPremiumOverride = premiumRepository.isDebugPremiumOverride,
                isPremium = premiumRepository.isPremium,
            )
        }
        if (enabled) cloudSyncCoordinator.syncIfEligible()
    }

    fun restorePurchases() {
        screenModelScope.launch {
            _state.update { it.copy(restoreInProgress = true, error = null) }
            val restored = billingRepository.restorePurchases()
            _state.update {
                it.copy(
                    restoreInProgress = false,
                    isPremium = premiumRepository.isPremium,
                    error = if (!restored) "No previous purchases found." else null,
                )
            }
            if (restored) cloudSyncCoordinator.syncIfEligible()
        }
    }

    fun exportPersonalInsights() {
        screenModelScope.launch {
            if (!premiumRepository.isPremium) {
                _state.update { it.copy(showPaywall = true) }
                return@launch
            }
            try {
                val insights = personalInsightRepository.getPersonalInsights().first()
                val json = buildString {
                    append("[\n")
                    insights.forEachIndexed { index, insight ->
                        if (index > 0) append(",\n")
                        append("  {")
                        append("\"id\":\"${insight.id}\",")
                        append("\"title\":${jsonString(insight.title)},")
                        append("\"body\":${jsonString(insight.body)},")
                        append("\"source\":${jsonString(insight.source.title)}")
                        append("}")
                    }
                    append("\n]")
                }
                // v1: confirm export built; platform share can follow
                _state.update {
                    it.copy(
                        exportMessage = "Export ready: ${insights.size} insights (${json.length} chars JSON).",
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Export failed.") }
            }
        }
    }

    fun signOut() {
        screenModelScope.launch {
            _state.update { it.copy(isBusy = true, error = null) }
            try {
                authRepository.signOut()
                _state.update { it.copy(isBusy = false, didLeaveAccount = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isBusy = false, error = e.message ?: "Sign out failed.")
                }
            }
        }
    }

    fun deleteAccount(password: String) {
        if (password.isBlank()) {
            _state.update { it.copy(error = "Enter your password to confirm deletion.") }
            return
        }
        screenModelScope.launch {
            _state.update { it.copy(isBusy = true, error = null) }
            try {
                val userId = authRepository.currentUserId
                    ?: throw IllegalStateException("Not signed in.")
                personalInsightRepository.deleteAllUserData(userId)
                authRepository.deleteAccount(password)
                _state.update { it.copy(isBusy = false, didLeaveAccount = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isBusy = false,
                        error = e.message
                            ?: "Could not delete account. Check your password and try again.",
                    )
                }
            }
        }
    }

    private fun jsonString(value: String): String =
        "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\""

    companion object {
        const val PRIVACY_URL = "https://www.markduenas.com/privacy"
        const val SUPPORT_URL = "https://www.markduenas.com"
        const val FREE_LIMIT = FREE_PERSONAL_INSIGHT_LIMIT
    }
}
