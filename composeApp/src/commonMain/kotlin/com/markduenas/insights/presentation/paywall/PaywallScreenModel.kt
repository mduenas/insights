package com.markduenas.insights.presentation.paywall

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.markduenas.insights.billing.BillingProduct
import com.markduenas.insights.billing.PurchaseResult
import com.markduenas.insights.data.BillingRepository
import com.markduenas.insights.domain.CloudSyncCoordinator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PaywallState(
    val products: List<BillingProduct> = emptyList(),
    val purchaseInProgress: Boolean = false,
    val restoreInProgress: Boolean = false,
    val error: String? = null,
    val didPurchaseOrRestore: Boolean = false,
)

class PaywallScreenModel(
    private val billingRepository: BillingRepository,
    private val cloudSyncCoordinator: CloudSyncCoordinator,
) : ScreenModel {

    private val _state = MutableStateFlow(PaywallState())
    val state: StateFlow<PaywallState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            billingRepository.products.collect { products ->
                _state.update { it.copy(products = products) }
            }
        }
    }

    fun purchase(productId: String) {
        screenModelScope.launch {
            _state.update { it.copy(purchaseInProgress = true, error = null) }
            when (val result = billingRepository.purchase(productId)) {
                is PurchaseResult.Success, is PurchaseResult.AlreadyOwned -> {
                    cloudSyncCoordinator.syncIfEligible()
                    _state.update {
                        it.copy(purchaseInProgress = false, didPurchaseOrRestore = true)
                    }
                }
                is PurchaseResult.Cancelled -> {
                    _state.update { it.copy(purchaseInProgress = false) }
                }
                is PurchaseResult.Error -> {
                    _state.update {
                        it.copy(purchaseInProgress = false, error = result.message)
                    }
                }
            }
        }
    }

    fun restore() {
        screenModelScope.launch {
            _state.update { it.copy(restoreInProgress = true, error = null) }
            val restored = billingRepository.restorePurchases()
            if (restored) {
                cloudSyncCoordinator.syncIfEligible()
                _state.update {
                    it.copy(restoreInProgress = false, didPurchaseOrRestore = true)
                }
            } else {
                _state.update {
                    it.copy(
                        restoreInProgress = false,
                        error = "No previous purchases found.",
                    )
                }
            }
        }
    }

    fun consumeSuccess() {
        _state.update { it.copy(didPurchaseOrRestore = false) }
    }
}
