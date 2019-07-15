package dev.olog.presentation.pro

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import dev.olog.core.interactor.ResetPreferencesUseCase
import dev.olog.presentation.BuildConfig
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.flowInterval
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

class BillingImpl @Inject constructor(
    activity: AppCompatActivity,
    private val resetPreferencesUseCase: ResetPreferencesUseCase,
    private val presentationPreferences: PresentationPreferencesGateway

) : BillingConnection(activity), IBilling, CoroutineScope by MainScope() {

    companion object {
        private const val PRO_VERSION_ID = "pro_version"
        @JvmStatic
        private val DEFAULT_PREMIUM = BuildConfig.DEBUG
        private const val DEFAULT_TRIAL = false
        @JvmStatic
        private val TRIAL_TIME = TimeUnit.HOURS.toMillis(1L)
    }

    private val premiumPublisher = ConflatedBroadcastChannel(DEFAULT_PREMIUM)
    private val trialPublisher = ConflatedBroadcastChannel(DEFAULT_TRIAL)

    private var isTrialState by Delegates.observable(DEFAULT_TRIAL) { _, _, new ->
        launch { trialPublisher.send(new) }
        if (!getBillingsState().isPremiumEnabled()) {
            setDefault()
        }
    }

    private var isPremiumState by Delegates.observable(DEFAULT_PREMIUM) { _, _, new ->
        launch { premiumPublisher.send(new) }
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
    }

    private fun isStillTrial(): Boolean {
        val packageInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
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

    @SuppressLint("SwitchIntDef")
    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        when (responseCode) {
            BillingClient.BillingResponse.OK -> {
                isPremiumState = isProBought(purchases)
            }
            // TODO add missing
//            else -> Log.w("Billing", "billing response code=$responseCode")
        }
    }

    private fun isProBought(purchases: MutableList<Purchase>?): Boolean {
        return purchases?.firstOrNull { it.sku == PRO_VERSION_ID } != null || BuildConfig.DEBUG
//        return true
    }

    override fun observeBillingsState(): Flow<BillingState> {
        return premiumPublisher.combineLatest(trialPublisher.asFlow()) { premium, trial ->
            BillingState(trial, premium)
        }.asObservable()
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

            billingClient.launchBillingFlow(activity, params)
        }
    }

    private fun setDefault() = launch(Dispatchers.Default) {
        resetPreferencesUseCase.execute()
        presentationPreferences.setDefault()
    }
}