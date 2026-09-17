package com.markduenas.insights.billing

sealed interface PurchaseResult {
    data object Success : PurchaseResult
    data object Cancelled : PurchaseResult
    data object AlreadyOwned : PurchaseResult
    data class Error(val message: String) : PurchaseResult
}
