package dev.olog.presentation.pro

import android.util.Log
import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.toast

internal abstract class BillingConnection(
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
                Log.v("Billing", "billing connection response code=${billingResult.responseCode}, " +
                        "error=${billingResult.debugMessage}")
                when (billingResult.responseCode){
                    BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> {
                        activity.toast(R.string.network_timeout)
                    }
                    BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {
                        activity.toast(R.string.network_not_available)
                    }
                }

                isConnected = billingResult.responseCode == BillingClient.BillingResponseCode.OK
                if (isConnected){
                    func?.invoke()
                }
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