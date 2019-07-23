package dev.olog.presentation.popup

import android.content.Intent
import android.media.audiofx.AudioEffect
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import dev.olog.presentation.R
import dev.olog.presentation.about.AboutActivity
import dev.olog.presentation.debug.DebugConfigurationActivity
import dev.olog.presentation.equalizer.EqualizerFragment
import dev.olog.presentation.navigator.superCerealTransition
import dev.olog.presentation.prefs.SettingsFragmentWrapper
import dev.olog.presentation.pro.IBilling
import dev.olog.presentation.sleeptimer.SleepTimerPickerDialogBuilder
import dev.olog.shared.extensions.toast
import javax.inject.Inject

class MainPopupNavigator @Inject constructor(
    private val activity: AppCompatActivity,
    private val billing: IBilling
) {

    fun toAboutActivity() {
        val intent = Intent(activity, AboutActivity::class.java)
        activity.startActivity(intent)
    }

    fun toEqualizer() {
        val useCustomEqualizer = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
                .getBoolean(activity.getString(R.string.prefs_used_equalizer_key), true)

        if (billing.getBillingsState().isPremiumEnabled() && useCustomEqualizer) {
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

    fun toDebugConfiguration() {
        val intent = Intent(activity, DebugConfigurationActivity::class.java)
        activity.startActivity(intent)
    }

    fun toSettingsActivity() {
        superCerealTransition(activity, SettingsFragmentWrapper(), SettingsFragmentWrapper.TAG)
    }

    fun toSleepTimer() {
        SleepTimerPickerDialogBuilder(activity, activity.supportFragmentManager).show()
    }

}