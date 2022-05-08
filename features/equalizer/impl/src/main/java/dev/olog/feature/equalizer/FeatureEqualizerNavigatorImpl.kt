package dev.olog.feature.equalizer

import android.content.Intent
import android.media.audiofx.AudioEffect
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import dev.olog.shared.extension.toast
import javax.inject.Inject

class FeatureEqualizerNavigatorImpl @Inject constructor(

) : FeatureEqualizerNavigator {

    override fun toEqualizer(activity: FragmentActivity) {
        val useCustomEqualizer = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
            .getBoolean(activity.getString(R.string.prefs_used_equalizer_key), true)

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
            activity.toast(R.string.equalizer_not_found)
        }
    }

}