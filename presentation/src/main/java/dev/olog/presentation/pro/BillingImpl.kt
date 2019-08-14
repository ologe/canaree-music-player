package dev.olog.presentation.pro

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.*
import dev.olog.core.interactor.ResetPreferencesUseCase
import dev.olog.core.prefs.AppPreferencesGateway
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
    activity: AppCompatActivity,
    private val billingPrefs: BillingPreferences,
    private val resetPreferencesUseCase: ResetPreferencesUseCase,
    private val presentationPreferences: PresentationPreferencesGateway,
    private val prefsGateway: AppPreferencesGateway

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
    private val lastShowAd = billingPrefs.getLastShowAd()

    private val premiumPublisher = ConflatedBroadcastChannel(lastPremium)
    private val trialPublisher = ConflatedBroadcastChannel(lastTrial)
    private val showAdPublisher = ConflatedBroadcastChannel(lastShowAd)

    private var isPremiumState by Delegates.observable(lastPremium) { _, _, new ->
        premiumPublisher.offer(new)
        billingPrefs.setLastPremium(new)
        if (!getBillingsState().isPremiumEnabled()) {
            setDefault()
        }
    }

    private var isTrialState by Delegates.observable(lastTrial) { _, _, new ->
        trialPublisher.offer(new)
        billingPrefs.setLastTrial(new)
        if (!getBillingsState().isPremiumEnabled()) {
            setDefault()
        }
    }

    private var isShowAdState by Delegates.observable(lastShowAd) { _, _, new ->
        showAdPublisher.offer(new)
        if (!getBillingsState().isPremiumEnabled()) {
            setDefault()
        }
    }

    init {
        doOnConnected { checkPurchases() }

        if (isStillTrial()) {
            isTrialState = true
            launch(Dispatchers.IO) {
                flowInterval(5, TimeUnit.MINUTES)
                    .map { isStillTrial() }
                    .onEach { isTrialState = it }
                    .takeWhile { it }
                    .collect { }
            }
        }
        launch {
            prefsGateway.observeCanShowAds()
                .flowOn(Dispatchers.IO)
                .collect {
                    isShowAdState = it
                    showAdPublisher.offer(it)
                }
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
        if (purchases.responseCode == BillingClient.BillingResponseCode.OK) {
            isPremiumState = isProBought(purchases.purchasesList)
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                isPremiumState = isProBought(purchases)
            }
            BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> {
                activity.get()?.toast(R.string.network_timeout)
            }
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {
                activity.get()?.toast(R.string.network_not_available)
            }

            else -> Log.w("Billing", "billing response code=${billingResult.responseCode}, " +
                    "error=${billingResult.debugMessage}")
        }
    }

    private fun isProBought(purchases: MutableList<Purchase>?): Boolean {
        return purchases?.find { it.sku == PRO_VERSION_ID } != null || BillingPreferences.DEFAULT_PREMIUM
//        return true
    }

    override fun observeBillingsState(): Flow<BillingState> {
        return combine(
            premiumPublisher.asFlow(),
            trialPublisher.asFlow(),
            showAdPublisher.asFlow()) { premium, trial, showAds ->
            BillingState(trial, premium, showAds)
        }.distinctUntilChanged()
    }

    override fun getBillingsState(): BillingState {
        return BillingState(
            isBought = premiumPublisher.value,
            isTrial = trialPublisher.value,
            canShowAd = showAdPublisher.value
        )
    }

    override fun purchasePremium() {
        val act = activity.get() ?: return
        doOnConnected {
            val params = SkuDetailsParams.newBuilder()
                .setSkusList(listOf(PRO_VERSION_ID))
                .setType(BillingClient.SkuType.INAPP)
                .build()

            billingClient.querySkuDetailsAsync(params) { result, skuDetailsList ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList?.isNotEmpty() == true) {
                    val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList[0])
                        .build()
                    billingClient.launchBillingFlow(act, flowParams)
                }
            }
        }
    }

    private fun setDefault() = launch(Dispatchers.Default) {
        resetPreferencesUseCase.execute()
        presentationPreferences.setDefault()
    }
}