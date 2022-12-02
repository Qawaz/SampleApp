package com.wakaztahir.sample.controller

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.wakaztahir.mindnode.controller.modules.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class RemoveAdsProduct @Inject constructor(
    @ApplicationContext context: Context,
    @ApplicationScope val scope : CoroutineScope
){
    companion object {
        private const val RemoveAdsProductId : String = "remove_ads"
    }

    var purchaseListener : ()->Unit = {}

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                scope.launch { handlePurchase(purchase) }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    private var isConnected = false

    private suspend fun startConnection() {
        if(billingClient.connectionState == BillingClient.ConnectionState.CONNECTED){
            return
        }else{
            isConnected = false
        }
        return suspendCoroutine {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        isConnected = true
                        it.resume(Unit)
                    }
                }

                override fun onBillingServiceDisconnected() {
                    isConnected = false
                }
            })
        }
    }

    suspend fun getRemoveAdsProduct(productId : String): ProductDetails? {

        startConnection()

        val productList = ArrayList<Product>().apply {
            add(Product.newBuilder()
                .setProductId(productId)
                .setProductType(ProductType.INAPP)
                .build())
        }

        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        // leverage queryProductDetails Kotlin extension function
        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params.build())
        }

        return productDetailsResult.productDetailsList?.firstOrNull()
    }

    suspend fun buyRemoveAdsProduct(activity: Activity) {

        val product = getRemoveAdsProduct(RemoveAdsProductId) ?: return

        val offerToken = product.subscriptionOfferDetails?.firstOrNull()?.offerToken

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                .setProductDetails(product)
                // to get an offer token, call ProductDetails.subscriptionOfferDetails()
                // for a list of offers that are available to the user
                .apply { offerToken?.let { setOfferToken(offerToken) }  }
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        // Launch the billing flow
        billingClient.launchBillingFlow(activity, billingFlowParams)

    }

    suspend fun isPurchased(): Boolean {
        startConnection()
        val history = billingClient.queryPurchaseHistory(
            QueryPurchaseHistoryParams.newBuilder().apply {
                this.setProductType(ProductType.INAPP)
            }.build()
        )
        history.purchaseHistoryRecordList?.forEach {
            if(it.products.isNotEmpty()){
                return true
            }
        }
        return false
    }

    private suspend fun handlePurchase(purchase: Purchase){
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
                }
            }
            purchaseListener()
        }
    }

}