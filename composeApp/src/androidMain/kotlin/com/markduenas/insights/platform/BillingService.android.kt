package com.markduenas.insights.platform

import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetailsResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.markduenas.insights.billing.BillingProduct
import com.markduenas.insights.billing.ProductIds
import com.markduenas.insights.billing.ProductType
import com.markduenas.insights.billing.PurchaseResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

private const val PREFS_NAME = "kindling_billing"
private const val KEY_PREMIUM = "premium_active"
private const val KEY_PRODUCT_ID = "premium_product_id"

actual class BillingService(private val context: Context) {

    private var purchaseContinuation: Continuation<PurchaseResult>? = null

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        val cont = purchaseContinuation
        purchaseContinuation = null

        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                val purchase = purchases?.firstOrNull()
                if (purchase != null) {
                    cont?.resume(PurchaseResult.Success)
                } else {
                    cont?.resume(PurchaseResult.Error("No purchase returned"))
                }
            }
            BillingResponseCode.USER_CANCELED ->
                cont?.resume(PurchaseResult.Cancelled)
            BillingResponseCode.ITEM_ALREADY_OWNED ->
                cont?.resume(PurchaseResult.AlreadyOwned)
            else ->
                cont?.resume(PurchaseResult.Error("Billing error: ${billingResult.debugMessage}"))
        }
    }

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    actual suspend fun connect(): Boolean = suspendCancellableCoroutine { cont ->
        if (billingClient.isReady) {
            cont.resume(true)
            return@suspendCancellableCoroutine
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                cont.resume(billingResult.responseCode == BillingResponseCode.OK)
            }

            override fun onBillingServiceDisconnected() {
                // reconnect on next operation
            }
        })
    }

    actual fun disconnect() {
        billingClient.endConnection()
    }

    actual suspend fun queryProducts(ids: List<String>): List<BillingProduct> {
        val products = mutableListOf<BillingProduct>()

        val subsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ids.map { id ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(id)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                }
            )
            .build()

        val subsResult: ProductDetailsResult = billingClient.queryProductDetails(subsParams)
        if (subsResult.billingResult.responseCode == BillingResponseCode.OK) {
            subsResult.productDetailsList?.forEach { details ->
                val offer = details.subscriptionOfferDetails?.firstOrNull()
                if (offer != null) {
                    val pricingPhase = offer.pricingPhases.pricingPhaseList.firstOrNull()
                    products.add(
                        BillingProduct(
                            id = details.productId,
                            title = details.title,
                            description = details.description,
                            formattedPrice = pricingPhase?.formattedPrice ?: "",
                            priceAmountMicros = pricingPhase?.priceAmountMicros ?: 0L,
                            priceCurrencyCode = pricingPhase?.priceCurrencyCode ?: "",
                            productType = ProductType.SUBSCRIPTION,
                        )
                    )
                }
            }
        }

        return products
    }

    actual suspend fun purchase(productId: String): PurchaseResult {
        val activity = ActivityProvider.activity
            ?: return PurchaseResult.Error("No activity available")

        val productType = BillingClient.ProductType.SUBS
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(productType)
                        .build()
                )
            )
            .build()

        val result = billingClient.queryProductDetails(params)
        val productDetails = result.productDetailsList?.firstOrNull()
            ?: return PurchaseResult.Error("Product not found: $productId")

        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
            ?: return PurchaseResult.Error("No subscription offer available")

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .setOfferToken(offerToken)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        return suspendCancellableCoroutine { cont ->
            purchaseContinuation = cont
            val launchResult = billingClient.launchBillingFlow(activity, billingFlowParams)
            if (launchResult.responseCode != BillingResponseCode.OK) {
                purchaseContinuation = null
                cont.resume(
                    PurchaseResult.Error(
                        "Failed to launch billing flow: ${launchResult.debugMessage}"
                    )
                )
            }
        }
    }

    actual suspend fun restorePurchases(): Boolean =
        checkPurchases(BillingClient.ProductType.SUBS)

    actual fun hasPremiumLocally(): Boolean =
        prefs.getBoolean(KEY_PREMIUM, false)

    actual suspend fun verifyEntitlement(): Boolean {
        val hasEntitlement = checkPurchases(BillingClient.ProductType.SUBS)
        setPremiumLocal(hasEntitlement, null)
        return hasEntitlement
    }

    private suspend fun checkPurchases(productType: String): Boolean {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(productType)
            .build()
        val result = billingClient.queryPurchasesAsync(params)
        if (result.billingResult.responseCode != BillingResponseCode.OK) return false

        for (purchase in result.purchasesList) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                val known = purchase.products.any { it in ProductIds.ALL }
                if (!known && purchase.products.isNotEmpty()) {
                    // Still accept any active sub in case product list expands
                }
                if (!purchase.isAcknowledged) {
                    val ackParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(ackParams)
                }
                return true
            }
        }
        return false
    }

    private fun setPremiumLocal(active: Boolean, productId: String?) {
        prefs.edit()
            .putBoolean(KEY_PREMIUM, active)
            .apply {
                if (productId != null) putString(KEY_PRODUCT_ID, productId)
            }
            .apply()
    }
}
