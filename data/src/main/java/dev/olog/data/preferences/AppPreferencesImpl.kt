package dev.olog.data.preferences

import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dev.olog.data.utils.edit
import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesImpl @Inject constructor(
        private val preferences: SharedPreferences,
        private val rxPreferences: RxSharedPreferences
) : AppPreferencesGateway {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"
        private const val FIRST_ACCESS = TAG + ".FIRST_ACCESS"
    }

    override fun isFirstAccess(): Boolean {
        val isFirstAccess = preferences.getBoolean(FIRST_ACCESS, true)

        if (isFirstAccess) {
            preferences.edit { putBoolean(FIRST_ACCESS, false) }
        }

        return isFirstAccess
    }

}