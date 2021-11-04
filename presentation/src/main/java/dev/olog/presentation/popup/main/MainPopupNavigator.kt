package dev.olog.presentation.popup.main

import android.content.Intent
import android.media.audiofx.AudioEffect
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import dev.olog.presentation.R
import dev.olog.feature.about.AboutFragment
import dev.olog.feature.base.superCerealTransition
import dev.olog.presentation.prefs.SettingsFragmentWrapper
import dev.olog.presentation.sleeptimer.SleepTimerPickerDialogBuilder
import dev.olog.shared.android.extensions.toast
import java.lang.ref.WeakReference
import javax.inject.Inject

class MainPopupNavigator @Inject constructor(
    activity: FragmentActivity,
) {

    private val activityRef = WeakReference(activity)

    fun toAboutActivity() {
        val activity = activityRef.get() ?: return

        superCerealTransition(activity, AboutFragment(), AboutFragment.TAG)
    }

    fun toEqualizer() {
        val activity = activityRef.get() ?: return

        val useCustomEqualizer = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
                .getBoolean(activity.getString(dev.olog.prefskeys.R.string.prefs_used_equalizer_key), true)

        if (useCustomEqualizer) {
            toBuiltInEqualizer()
        } else {
            searchForEqualizer()
        }
    }

    private fun toBuiltInEqualizer() {
//        val activity = activityRef.get() ?: return TODO
//
//        val instance = EqualizerFragment.newInstance()
//        instance.show(activity.supportFragmentManager, EqualizerFragment.TAG)
    }

    private fun searchForEqualizer() {
        val activity = activityRef.get() ?: return

        val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {
            activity.toast(R.string.equalizer_not_found)
        }
    }

    fun toSettingsActivity() {
        val activity = activityRef.get() ?: return

        superCerealTransition(activity, SettingsFragmentWrapper(), SettingsFragmentWrapper.TAG)
    }

    fun toSleepTimer() {
        val activity = activityRef.get() ?: return

        SleepTimerPickerDialogBuilder(activity, activity.supportFragmentManager).show()
    }

}