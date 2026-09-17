package com.markduenas.insights.platform

import com.markduenas.insights.billing.BillingProduct
import com.markduenas.insights.billing.ProductType
import com.markduenas.insights.billing.PurchaseResult
import platform.Foundation.NSUserDefaults
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val KEY_PREMIUM = "kindling_premium_active"
private const val KEY_PRODUCT_ID = "kindling_premium_product_id"

actual class BillingService {

    private val defaults = NSUserDefaults.standardUserDefaults

    actual suspend fun connect(): Boolean = true

    actual fun disconnect() {}

    actual suspend fun queryProducts(ids: List<String>): List<BillingProduct> =
        suspendCoroutine { cont ->
            StoreKitBridge.fetchProducts(ids) { products ->
                val result = products?.map { dto ->
                    BillingProduct(
                        id = dto.productId,
                        title = dto.title,
                        description = dto.productDescription,
                        formattedPrice = dto.formattedPrice,
                        priceAmountMicros = (dto.priceAmount * 1_000_000).toLong(),
                        priceCurrencyCode = dto.currencyCode,
                        productType = if (dto.isSubscription) {
                            ProductType.SUBSCRIPTION
                        } else {
                            ProductType.ONE_TIME
                        },
                    )
                } ?: emptyList()
                cont.resume(result)
            }
        }

    actual suspend fun purchase(productId: String): PurchaseResult =
        suspendCoroutine { cont ->
            StoreKitBridge.purchase(productId) { statusCode ->
                val result = when (statusCode.toInt()) {
                    0 -> PurchaseResult.Success
                    1 -> PurchaseResult.Cancelled
                    2 -> PurchaseResult.AlreadyOwned
                    else -> PurchaseResult.Error("StoreKit purchase failed (code $statusCode)")
                }
                cont.resume(result)
            }
        }

    actual suspend fun restorePurchases(): Boolean =
        suspendCoroutine { cont ->
            StoreKitBridge.restorePurchases { hasPremium ->
                cont.resume(hasPremium)
            }
        }

    actual fun hasPremiumLocally(): Boolean =
        defaults.boolForKey(KEY_PREMIUM)

    actual suspend fun verifyEntitlement(): Boolean {
        val hasEntitlement = restorePurchases()
        setPremiumLocal(hasEntitlement, null)
        return hasEntitlement
    }

    private fun setPremiumLocal(active: Boolean, productId: String?) {
        defaults.setBool(active, forKey = KEY_PREMIUM)
        if (productId != null) {
            defaults.setObject(productId, forKey = KEY_PRODUCT_ID)
        }
    }
}
