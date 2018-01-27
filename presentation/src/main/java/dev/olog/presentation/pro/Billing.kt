package dev.olog.presentation.pro

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.vending.billing.IInAppBillingService
import dev.olog.presentation.dagger.ActivityContext
import dev.olog.presentation.dagger.ActivityLifecycle
import dev.olog.shared_android.interfaces.pro.IBilling
import javax.inject.Inject


class Billing @Inject constructor (
        @ActivityContext private val context: Context,
        @ActivityLifecycle lifecycle: Lifecycle

): IBilling, PurchasesUpdatedListener, DefaultLifecycleObserver {

    private val billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .build()

    private var inAppBillingService: IInAppBillingService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            inAppBillingService = IInAppBillingService.Stub.asInterface(service)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            inAppBillingService = null
        }
    }

    init {
        lifecycle.addObserver(this)
    }

    override fun bindToService(){
        val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
        serviceIntent.setPackage("com.android.vending")
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        inAppBillingService?.let { context.unbindService(serviceConnection) }
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {

    }
}