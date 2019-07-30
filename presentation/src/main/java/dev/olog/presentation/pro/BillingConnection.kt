package dev.olog.presentation.pro

import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import dev.olog.shared.android.extensions.toast

abstract class BillingConnection(
    protected val activity: FragmentActivity
) : PurchasesUpdatedListener, DefaultLifecycleObserver {

    init {
        activity.lifecycle.addObserver(this)
    }

    private var isConnected = false

    protected val billingClient: BillingClient = BillingClient.newBuilder(activity)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    protected fun doOnConnected(func: (() -> Unit)?) {
        if (isConnected) {
            func?.invoke()
            return
        }

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> isConnected = true
                    BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> activity.toast("Play store not found")
                }
                func?.invoke()
            }

            override fun onBillingServiceDisconnected() {
                isConnected = false
            }
        })
    }

    @CallSuper
    override fun onDestroy(owner: LifecycleOwner) {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

}