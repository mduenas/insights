package com.markduenas.insights.platform

import com.markduenas.insights.billing.BillingProduct
import com.markduenas.insights.billing.PurchaseResult

expect class BillingService {
    suspend fun connect(): Boolean
    fun disconnect()
    suspend fun queryProducts(ids: List<String>): List<BillingProduct>
    suspend fun purchase(productId: String): PurchaseResult
    suspend fun restorePurchases(): Boolean
    fun hasPremiumLocally(): Boolean
    suspend fun verifyEntitlement(): Boolean
}
