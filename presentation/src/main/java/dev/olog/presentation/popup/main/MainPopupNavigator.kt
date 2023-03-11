package dev.olog.presentation.popup.main

import android.content.Intent
import android.media.audiofx.AudioEffect
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import dev.olog.presentation.R
import dev.olog.presentation.about.AboutFragment
import dev.olog.presentation.equalizer.EqualizerFragment
import dev.olog.presentation.navigator.superCerealTransition
import dev.olog.presentation.prefs.SettingsFragmentWrapper
import dev.olog.presentation.sleeptimer.SleepTimerPickerDialogBuilder
import dev.olog.platform.extension.toast
import javax.inject.Inject

class MainPopupNavigator @Inject constructor(
    private val activity: FragmentActivity,
) {

    fun toAboutActivity() {
        superCerealTransition(activity, AboutFragment(), AboutFragment.TAG)
    }

    fun toEqualizer() {
        val useCustomEqualizer = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
                .getBoolean(activity.getString(R.string.prefs_used_equalizer_key), true)

        if (useCustomEqualizer) {
            toBuiltInEqualizer()
        } else {
            searchForEqualizer()
        }
    }

    private fun toBuiltInEqualizer() {
        val instance = EqualizerFragment.newInstance()
        instance.show(activity.supportFragmentManager, EqualizerFragment.TAG)
    }

    private fun searchForEqualizer() {
        val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {
            activity.toast(R.string.equalizer_not_found)
        }
    }

    fun toSettingsActivity() {
        superCerealTransition(activity, SettingsFragmentWrapper(), SettingsFragmentWrapper.TAG)
    }

    fun toSleepTimer() {
        SleepTimerPickerDialogBuilder(activity, activity.supportFragmentManager).show()
    }

}