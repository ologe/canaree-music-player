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
    private val billingPrefs: BillingPreferences,
    private val resetPreferencesUseCase: ResetPreferencesUseCase,
    private val presentationPreferences: PresentationPreferencesGateway

) : BillingConnection(WeakReference(activity)), IBilling, CoroutineScope by MainScope() {

    companion object {
        @JvmStatic
        private val TRIAL_TIME = TimeUnit.HOURS.toMillis(1L)

        private const val PRO_VERSION_ID = "pro_version"

        private const val TEST_PURCHASED = "android.test.purchased"
        private const val TEST_CANCELLED = "android.test.canceled"
        private const val TEST_UNAVAILABLE = "android.test.item_unavailable"
    }

    private val lastPremium = billingPrefs.getLastPremium()
    private val lastTrial = billingPrefs.getLastTrial()

    private val premiumPublisher = ConflatedBroadcastChannel(lastPremium)
    private val trialPublisher = ConflatedBroadcastChannel(lastTrial)

    private var isPremiumState by Delegates.observable(lastPremium) { _, _, new ->
        premiumPublisher.trySend(new)
        billingPrefs.setLastPremium(new)
        if (!getBillingsState().isPremiumEnabled()) {
            setDefault()
        }
    }

    private var isTrialState by Delegates.observable(lastTrial) { _, _, new ->
        trialPublisher.trySend(new)
        billingPrefs.setLastTrial(new)
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
        billingPrefs.setLastPremium(isPremiumState)
        billingPrefs.setLastTrial(isTrialState)
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
        return purchases?.find { it.sku == PRO_VERSION_ID } != null || BillingPreferences.DEFAULT_PREMIUM
//        return true
    }

    override fun observeBillingsState(): Flow<BillingState> {
        return combine(
            premiumPublisher.asFlow(),
            trialPublisher.asFlow()
        ) { premium, trial ->
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