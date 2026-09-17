package com.markduenas.insights.billing

data class BillingProduct(
    val id: String,
    val title: String,
    val description: String,
    val formattedPrice: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String,
    val productType: ProductType,
)

enum class ProductType {
    ONE_TIME,
    SUBSCRIPTION,
}

object ProductIds {
    const val PREMIUM_MONTHLY = "premium_monthly"
    const val PREMIUM_YEARLY = "premium_yearly"

    val ALL = listOf(PREMIUM_MONTHLY, PREMIUM_YEARLY)
}

/** Fallback display prices when store products are not yet loaded. */
object FallbackPricing {
    const val MONTHLY = "$4.99"
    const val YEARLY = "$39.99"

    fun forProduct(id: String): String = when (id) {
        ProductIds.PREMIUM_YEARLY -> YEARLY
        else -> MONTHLY
    }
}

/** Free tier personal insight cap (new saves blocked at this count). */
const val FREE_PERSONAL_INSIGHT_LIMIT = 25
