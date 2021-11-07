package dev.olog.feature.equalizer

import android.content.Intent
import android.media.audiofx.AudioEffect
import androidx.fragment.app.FragmentActivity
import dev.olog.shared.android.extensions.toast
import javax.inject.Inject

class FeatureEqualizerNavigatorImpl @Inject constructor(
    private val prefs: EqualizerPrefs,
) : FeatureEqualizerNavigator {

    override fun toEqualizer(activity: FragmentActivity) {
        val useCustomEqualizer = prefs.useCustomEqualizer.get()

        if (useCustomEqualizer) {
            toBuiltInEqualizer(activity)
        } else {
            searchForEqualizer(activity)
        }
    }

    private fun toBuiltInEqualizer(activity: FragmentActivity) {
        val instance = EqualizerFragment.newInstance()
        instance.show(activity.supportFragmentManager, EqualizerFragment.TAG)
    }

    private fun searchForEqualizer(activity: FragmentActivity) {
        val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {
            activity.toast(localization.R.string.equalizer_not_found)
        }
    }

}