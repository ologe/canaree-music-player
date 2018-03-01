package dev.olog.msc.interfaces.pro

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.android.billingclient.api.*
import javax.inject.Inject

interface IBilling {

    fun isPremium(): Boolean
    fun purchasePremium()

}

private const val PRO_VERSION_ID = "pro_version"

class BillingImpl @Inject constructor(
        private val activity: AppCompatActivity

) : IBilling, PurchasesUpdatedListener, BillingClientStateListener, DefaultLifecycleObserver {

    private var isPremium = false

    private val billingClient = BillingClient.newBuilder(activity)
            .setListener(this)
            .build()

    init {
        // todo retry policy
        activity.lifecycle.addObserver(this)
        billingClient.startConnection(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        billingClient.endConnection()
    }

    override fun onBillingSetupFinished(responseCode: Int) {
        println("onBillingSetupFinished with response code:$responseCode")
        val purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        if (purchases.responseCode == BillingClient.BillingResponse.OK){
            isPremium = isProBought(purchases.purchasesList)
        }
    }

    override fun onBillingServiceDisconnected() {

    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        when (responseCode){
            BillingClient.BillingResponse.OK -> {
                println("purchased")
                isPremium = isProBought(purchases)
            }
            BillingClient.BillingResponse.USER_CANCELED -> {
                println("user cancelled purchasing flow")
                // Handle an error caused by a user cancelling the purchase flow.
            }
            else -> Log.w("Billing", "billing response code=$responseCode")
        }
    }

    private fun isProBought(purchases: MutableList<Purchase>?): Boolean {
        return purchases?.firstOrNull { it.sku == PRO_VERSION_ID } != null
    }

//    override fun isPremium(): Boolean = isPremium
    override fun isPremium(): Boolean = true

    override fun purchasePremium() {
        val params = BillingFlowParams.newBuilder()
                .setSku(PRO_VERSION_ID)
                .setType(BillingClient.SkuType.INAPP)
                .build()

        billingClient.launchBillingFlow(activity, params)
    }
}