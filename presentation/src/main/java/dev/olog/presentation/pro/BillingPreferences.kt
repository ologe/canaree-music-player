package dev.olog.presentation.pro

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class BillingPreferences @Inject constructor(
    private val prefs: SharedPreferences
) {

    companion object {
        @JvmStatic
        val DEFAULT_PREMIUM = false
        private const val DEFAULT_TRIAL = false

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


}