package com.markduenas.insights.data

import com.markduenas.insights.billing.BillingProduct
import com.markduenas.insights.billing.ProductIds
import com.markduenas.insights.billing.PurchaseResult
import com.markduenas.insights.platform.BillingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingRepository(
    private val billingService: BillingService,
    private val premiumRepository: PremiumRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _products = MutableStateFlow<List<BillingProduct>>(emptyList())
    val products: StateFlow<List<BillingProduct>> = _products.asStateFlow()

    val premiumActive: StateFlow<Boolean> = premiumRepository.premiumActive

    fun initialize() {
        scope.launch {
            try {
                if (!billingService.connect()) return@launch
                _products.value = billingService.queryProducts(ProductIds.ALL)
                val verified = billingService.verifyEntitlement()
                premiumRepository.setPremiumActive(verified)
            } catch (_: Exception) {
                // Free tier remains usable
            }
        }
    }

    suspend fun purchase(productId: String): PurchaseResult {
        val result = billingService.purchase(productId)
        if (result is PurchaseResult.Success || result is PurchaseResult.AlreadyOwned) {
            premiumRepository.setPremiumActive(true, productId)
        }
        return result
    }

    suspend fun restorePurchases(): Boolean {
        val restored = billingService.restorePurchases()
        premiumRepository.setPremiumActive(restored)
        return restored
    }

    fun isPremium(): Boolean = premiumRepository.isPremium

    fun disconnect() {
        billingService.disconnect()
    }
}
