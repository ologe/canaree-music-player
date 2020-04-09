package dev.olog.presentation.model

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.navigation.screens.BottomNavigationPage
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.observeKey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class PresentationPreferencesImpl @Inject constructor(
    private val context: Context,
    private val preferences: SharedPreferences
) : PresentationPreferencesGateway {

    companion object {
        private const val TAG = "AppPreferencesDataStoreImpl"

        private const val FIRST_ACCESS = "$TAG.FIRST_ACCESS"



    }

    override fun isFirstAccess(): Boolean {
        val isFirstAccess = preferences.getBoolean(FIRST_ACCESS, true)

        if (isFirstAccess) {
            preferences.edit { putBoolean(FIRST_ACCESS, false) }
        }

        return isFirstAccess
    }


    override fun observePlayerControlsVisibility(): Flow<Boolean> {
        return preferences.observeKey(context.getString(R.string.prefs_player_controls_visibility_key), false)
    }

    override fun isAdaptiveColorEnabled(): Boolean {
        return preferences.getBoolean(context.getString(R.string.prefs_adaptive_colors_key), false)
    }




}