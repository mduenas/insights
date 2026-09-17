package com.markduenas.insights.data

import com.markduenas.insights.platform.PlatformSettings
import com.markduenas.insights.platform.isDebugBuild
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PremiumRepository(private val settings: PlatformSettings) {

    private val storePremium = MutableStateFlow(settings.getBoolean(KEY_PREMIUM_ACTIVE, false))
    private val debugPremium = MutableStateFlow(
        isDebugBuild() && settings.getBoolean(KEY_DEBUG_PREMIUM, false)
    )

    private val _premiumActive = MutableStateFlow(computeEffective())
    val premiumActive: StateFlow<Boolean> = _premiumActive.asStateFlow()

    val isPremium: Boolean get() = _premiumActive.value

    val isDebugPremiumOverride: Boolean get() = debugPremium.value

    val showDebugPremiumToggle: Boolean get() = isDebugBuild()

    /**
     * Called after store purchase, restore, or entitlement verification.
     * Does not clear a debug override (debug can still force premium on).
     */
    fun setPremiumActive(active: Boolean, productId: String? = null) {
        storePremium.value = active
        settings.putBoolean(KEY_PREMIUM_ACTIVE, active)
        if (productId != null) {
            settings.putString(KEY_PREMIUM_PRODUCT_ID, productId)
        }
        publish()
    }

    /** Debug/QA only — no-op on release builds. */
    fun setDebugPremiumOverride(active: Boolean) {
        if (!isDebugBuild()) return
        debugPremium.value = active
        settings.putBoolean(KEY_DEBUG_PREMIUM, active)
        publish()
    }

    fun premiumProductId(): String = settings.getString(KEY_PREMIUM_PRODUCT_ID, "")

    private fun computeEffective(): Boolean =
        storePremium.value || (isDebugBuild() && debugPremium.value)

    private fun publish() {
        _premiumActive.value = computeEffective()
    }

    companion object {
        private const val KEY_PREMIUM_ACTIVE = "premium_active"
        private const val KEY_PREMIUM_PRODUCT_ID = "premium_product_id"
        private const val KEY_DEBUG_PREMIUM = "debug_premium_override"
    }
}
