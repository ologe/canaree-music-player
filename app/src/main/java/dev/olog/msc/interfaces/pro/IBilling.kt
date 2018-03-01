package dev.olog.msc.interfaces.pro

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import javax.inject.Inject

interface IBilling {

    fun isPremium(): Boolean
    fun purchasePremium()

}

private const val PRO_VERSION_ID = "pro_version"

class BillingImpl @Inject constructor(
        private val activity: Activity

) : IBilling, PurchasesUpdatedListener, BillingClientStateListener {

    private var isPremium = false

    private val billingClient = BillingClient.newBuilder(activity)
            .setListener(this)
            .build()

    init {
        // todo retry policy
        billingClient.startConnection(this)
    }

    override fun onBillingSetupFinished(responseCode: Int) {
        println("onBillingSetupFinished with response:$responseCode")
    }

    override fun onBillingServiceDisconnected() {

    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        when (responseCode){
            BillingClient.BillingResponse.OK -> {
                println("purchased")
                purchases?.forEach {
                    isPremium = it.orderId == PRO_VERSION_ID
                }
            }
            BillingClient.BillingResponse.USER_CANCELED -> {
                println("user cancelled purchasing flow")
                // Handle an error caused by a user cancelling the purchase flow.
            }
            else -> Log.w("Billing", "billing response code=$responseCode")
        }
    }

    override fun isPremium(): Boolean = isPremium

    override fun purchasePremium() {
        val params = BillingFlowParams.newBuilder()
                .setSku(PRO_VERSION_ID)
                .setType(BillingClient.SkuType.INAPP)
                .build()

        billingClient.launchBillingFlow(activity, params)
    }
}