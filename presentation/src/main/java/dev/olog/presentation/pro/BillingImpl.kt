package dev.olog.presentation.pro

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import dev.olog.core.interactor.ResetPreferencesUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.flowInterval
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

internal class BillingImpl @Inject constructor(
    activity: FragmentActivity,
    private val resetPreferencesUseCase: ResetPreferencesUseCase,
    private val presentationPreferences: PresentationPreferencesGateway

) : BillingConnection(WeakReference(activity)), IBilling, CoroutineScope by MainScope() {

    companion object {
        @JvmStatic
        private val TRIAL_TIME = TimeUnit.HOURS.toMillis(1L)
        private const val DEFAULT_PREMIUM = false
        private const val DEFAULT_TRIAL = false

        private const val PRO_VERSION_ID = "pro_version"

        private const val TEST_PURCHASED = "android.test.purchased"
        private const val TEST_CANCELLED = "android.test.canceled"
        private const val TEST_UNAVAILABLE = "android.test.item_unavailable"
    }

    private val premiumPublisher = ConflatedBroadcastChannel(DEFAULT_PREMIUM)
    private val trialPublisher = ConflatedBroadcastChannel(DEFAULT_TRIAL)

    private var isPremiumState by Delegates.observable(DEFAULT_PREMIUM) { _, _, new ->
        premiumPublisher.offer(new)
        if (!getBillingsState().isPremiumEnabled()) {
            setDefault()
        }
    }

    private var isTrialState by Delegates.observable(DEFAULT_TRIAL) { _, _, new ->
        trialPublisher.offer(new)
        if (!getBillingsState().isPremiumEnabled()) {
            setDefault()
        }
    }

    init {
        activity.lifecycle.addObserver(this)
        doOnConnected { checkPurchases() }

        launch(Dispatchers.IO) {
            flowInterval(1, TimeUnit.SECONDS)
                .map { isStillTrial() }
                .onEach { isTrialState = it }
                .takeWhile { it }
                .collect { }
        }
    }

    private fun isStillTrial(): Boolean {
        val act = activity.get() ?: return false
        val packageInfo = act.packageManager.getPackageInfo(act.packageName, 0)
        val firstInstallTime = packageInfo.firstInstallTime
        return System.currentTimeMillis() - firstInstallTime < TRIAL_TIME
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        cancel()
    }

    private fun checkPurchases() {
        val purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        if (purchases.responseCode == BillingClient.BillingResponse.OK) {
            isPremiumState = isProBought(purchases.purchasesList)
        }
    }

    override fun onPurchasesUpdated(
        responseCode: Int,
        purchases: MutableList<Purchase>?
    ) {
        when (responseCode) {
            BillingClient.BillingResponse.OK -> {
                isPremiumState = isProBought(purchases)
            }
            BillingClient.BillingResponse.SERVICE_TIMEOUT -> {
                activity.get()?.toast(R.string.network_timeout)
            }
            BillingClient.BillingResponse.SERVICE_UNAVAILABLE -> {
                activity.get()?.toast(R.string.common_no_internet)
            }
            else -> Log.w("Billing", "billing response code=${responseCode}")
        }
    }

    private fun isProBought(purchases: MutableList<Purchase>?): Boolean {
        return purchases?.find { it.sku == PRO_VERSION_ID } != null || DEFAULT_PREMIUM
    }

    override fun observeBillingsState(): Flow<BillingState> {
        return combine(
            premiumPublisher.asFlow(),
            trialPublisher.asFlow()) { premium, trial ->
            BillingState(trial, premium)
        }.distinctUntilChanged()
    }

    override fun getBillingsState(): BillingState {
        return BillingState(
            isBought = premiumPublisher.value,
            isTrial = trialPublisher.value
        )
    }

    override fun purchasePremium() {
        doOnConnected {
            val params = BillingFlowParams.newBuilder()
                .setSku(PRO_VERSION_ID)
                .setType(BillingClient.SkuType.INAPP)
                .build()

            activity.get()?.let {
                billingClient.launchBillingFlow(it, params)
            }
        }
    }

    private fun setDefault() = launch(Dispatchers.Default) {
        resetPreferencesUseCase.execute()
        presentationPreferences.setDefault()
    }
}