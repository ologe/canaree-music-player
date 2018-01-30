package dev.olog.presentation.pro

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import dev.olog.presentation.dagger.ActivityContext
import dev.olog.presentation.dagger.ActivityLifecycle
import dev.olog.shared_android.interfaces.pro.IBilling
import javax.inject.Inject


class Billing @Inject constructor (
        @ActivityContext private val context: Context,
        @ActivityLifecycle lifecycle: Lifecycle

): IBilling, BillingClientStateListener, PurchasesUpdatedListener, DefaultLifecycleObserver {

    private var state : State = State.NOT_CONNECTED

    private val billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .build()


    init {
        lifecycle.addObserver(this)
        connect()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (billingClient.isReady){
            billingClient.endConnection()
        }
    }

    private fun connect(){
        if (state == State.NOT_CONNECTED){
            state = State.CONNECTING
            billingClient.startConnection(this)
        }
    }

    override fun onBillingSetupFinished(responseCode: Int) {
        if (responseCode == BillingClient.BillingResponse.OK){
            // The billing client is ready. You can query purchases here.
            state = State.CONNECTED
        }
    }


    override fun onBillingServiceDisconnected() {
        println("onBillingServiceDisconnected")
        state = State.ERROR
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
    }

    override fun isPremium(): Boolean {
        val queryPurchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        if (queryPurchases.responseCode == BillingClient.BillingResponse.OK && queryPurchases.purchasesList != null){
            for (purchase in queryPurchases.purchasesList) {
                val sku = purchase.sku
                return sku == "pro_version"
            }
        }
        return false
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        println("onPurchasesUpdated")
        when (responseCode){
            BillingClient.BillingResponse.OK -> {
//                purchases?.let {
//                    for (purchase in it) {
//                        println(purchase)
//                    }
//                }
            }
            BillingClient.BillingResponse.USER_CANCELED -> {
                // Handle an error caused by a user cancelling the purchase flow.
            }
            else -> {
                // Handle any other error codes.
            }
        }
    }

    private enum class State {
        NOT_CONNECTED, CONNECTING, CONNECTED, ERROR
    }
}