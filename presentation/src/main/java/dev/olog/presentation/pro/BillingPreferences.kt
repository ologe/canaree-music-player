package dev.olog.presentation.pro

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.core.dagger.ApplicationContext
import dev.olog.presentation.BuildConfig
import dev.olog.presentation.R
import javax.inject.Inject

internal class BillingPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferences
) {

    companion object {
        @JvmStatic
        val DEFAULT_PREMIUM = false
        private const val DEFAULT_TRIAL = false
        private const val DEFAULT_SHOW_AD = false

        private const val LAST_PREMIUM = "LAST_PREMIUM"
        private const val LAST_TRIAL = "LAST_TRIAL"
    }

    fun getLastPremium(): Boolean {
        return prefs.getBoolean(LAST_PREMIUM, DEFAULT_PREMIUM)
    }

    fun setLastPremium(enabled: Boolean){
        prefs.edit {
            putBoolean(LAST_PREMIUM, enabled)
        }
    }

    fun getLastTrial(): Boolean {
        return prefs.getBoolean(LAST_TRIAL, DEFAULT_TRIAL)
    }

    fun setLastTrial(enabled: Boolean){
        prefs.edit {
            putBoolean(LAST_TRIAL, enabled)
        }
    }

    fun getLastShowAd(): Boolean {
        return prefs.getBoolean(context.getString(R.string.premium_ad_key), DEFAULT_SHOW_AD)
    }

}