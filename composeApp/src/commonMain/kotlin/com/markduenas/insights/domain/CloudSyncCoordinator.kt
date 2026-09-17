package com.markduenas.insights.domain

import com.markduenas.insights.data.PremiumRepository
import com.markduenas.insights.domain.repository.AuthRepository
import com.markduenas.insights.domain.repository.PersonalInsightRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Runs personal insight cloud pull + push when the user is Premium and signed in.
 */
class CloudSyncCoordinator(
    private val personalRepo: PersonalInsightRepository,
    private val authRepository: AuthRepository,
    private val premiumRepository: PremiumRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun syncIfEligible() {
        scope.launch {
            if (!premiumRepository.isPremium) return@launch
            val uid = authRepository.currentUserId ?: return@launch
            try {
                personalRepo.pullRemotePersonalInsights(uid)
                personalRepo.syncPendingInsights()
            } catch (_: Exception) {
                // Best-effort
            }
        }
    }
}
