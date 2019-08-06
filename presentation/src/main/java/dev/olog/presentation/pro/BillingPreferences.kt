package dev.olog.presentation.pro

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.presentation.BuildConfig
import javax.inject.Inject

internal class BillingPreferences @Inject constructor(
    private val prefs: SharedPreferences
) {

    companion object {
        @JvmStatic
        val DEFAULT_PREMIUM = BuildConfig.DEBUG
        private const val DEFAULT_TRIAL = false
        private const val DEFAULT_SHOW_AD = false

        private const val LAST_PREMIUM = "LAST_PREMIUM"
        private const val LAST_TRIAL = "LAST_TRIAL"
        private const val LAST_SHOW_AD = "LAST_SHOW_AD"
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
        return prefs.getBoolean(LAST_SHOW_AD, DEFAULT_SHOW_AD)
    }

    fun setLastShowAd(enabled: Boolean){
        prefs.edit {
            putBoolean(LAST_SHOW_AD, enabled)
        }
    }

}