package dev.olog.presentation.popup.main

import android.content.Intent
import android.media.audiofx.AudioEffect
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.preference.PreferenceManager
import dev.olog.presentation.R
import dev.olog.presentation.about.AboutFragment
import dev.olog.navigation.transition.setupEnterAnimation
import dev.olog.navigation.transition.setupExitAnimation
import dev.olog.presentation.equalizer.EqualizerFragment
import dev.olog.presentation.navigator.allowed
import dev.olog.navigation.findFirstVisibleFragment
import dev.olog.presentation.sleeptimer.SleepTimerPickerDialogBuilder
import dev.olog.feature.presentation.base.extensions.toast
import dev.olog.shared.mandatory
import javax.inject.Inject

internal class MainPopupNavigator @Inject constructor(
    private val activity: FragmentActivity
) {

    fun toAboutActivity() {
        val current =
            findFirstVisibleFragment(activity.supportFragmentManager)
        current!!.setupExitAnimation(activity)

        val fragment = AboutFragment()
        fragment.setupEnterAnimation(activity)

        activity.supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment, AboutFragment.TAG)
            addToBackStack(AboutFragment.TAG)
        }
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
        mandatory(allowed()) ?: return


    }

    fun toSleepTimer() {
        mandatory(allowed()) ?: return

        SleepTimerPickerDialogBuilder(activity, activity.supportFragmentManager).show()
    }

}