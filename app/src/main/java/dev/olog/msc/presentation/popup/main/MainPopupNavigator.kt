package dev.olog.msc.presentation.popup.main

import android.content.Intent
import android.media.audiofx.AudioEffect
import android.preference.PreferenceManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import androidx.core.widget.toast
import dev.olog.msc.R
import dev.olog.msc.presentation.about.AboutActivity
import dev.olog.msc.presentation.debug.DebugConfigurationActivity
import dev.olog.msc.presentation.dialog.sleep.timer.SleepTimerPickerDialogBuilder
import dev.olog.msc.presentation.equalizer.EqualizerFragment
import dev.olog.msc.presentation.library.categories.CategoriesFragment
import dev.olog.msc.presentation.preferences.PreferencesActivity
import dev.olog.msc.pro.IBilling
import dev.olog.msc.utils.k.extension.fragmentTransaction
import javax.inject.Inject

class MainPopupNavigator @Inject constructor(
        private val activity: AppCompatActivity,
        private val billing: IBilling
) {

    fun toAboutActivity() {
        val intent = Intent(activity, AboutActivity::class.java)
        activity.startActivity(intent)
    }

    fun toEqualizer(){
        val useAppEqualizer = PreferenceManager.getDefaultSharedPreferences(activity)
                .getBoolean(activity.getString(R.string.prefs_used_equalizer_key), true)

        if (billing.isPremium() && useAppEqualizer){
            toBuiltInEqualizer()
        } else {
            searchForEqualizer()
        }
    }

    private fun toBuiltInEqualizer(){

        val categoriesFragment = activity.supportFragmentManager
                .findFragmentByTag(CategoriesFragment.TAG) ?: return

        activity.fragmentTransaction {
            setReorderingAllowed(true)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            hide(categoriesFragment)
            add(R.id.fragmentContainer, EqualizerFragment(), EqualizerFragment.TAG)
            addToBackStack(EqualizerFragment.TAG)
        }
    }

    private fun searchForEqualizer(){
        val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
        if (intent.resolveActivity(activity.packageManager) != null){
            activity.startActivity(intent)
        } else {
            activity.toast(R.string.equalizer_not_found)
        }
    }

    fun toDebugConfiguration(){
        val intent = Intent(activity, DebugConfigurationActivity::class.java)
        activity.startActivity(intent)
    }

    fun toSettingsActivity(){
        val intent = Intent(activity, PreferencesActivity::class.java)
        activity.startActivityForResult(intent, PreferencesActivity.REQUEST_CODE)
    }

    fun toSleepTimer(){
        SleepTimerPickerDialogBuilder(activity.supportFragmentManager)
//                .setColorSelected(R.color.item_selected)
                .show()
    }

}