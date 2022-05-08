package dev.olog.presentation.popup.main

import androidx.fragment.app.FragmentActivity
import dev.olog.presentation.about.AboutFragment
import dev.olog.presentation.navigator.superCerealTransition
import dev.olog.presentation.prefs.SettingsFragmentWrapper
import dev.olog.presentation.sleeptimer.SleepTimerPickerDialogBuilder
import javax.inject.Inject

class MainPopupNavigator @Inject constructor(
    private val activity: FragmentActivity,
) {

    fun toAboutActivity() {
        superCerealTransition(activity, AboutFragment(), AboutFragment.TAG)
    }

    fun toSettingsActivity() {
        superCerealTransition(activity, SettingsFragmentWrapper(), SettingsFragmentWrapper.TAG)
    }

    fun toSleepTimer() {
        SleepTimerPickerDialogBuilder(activity, activity.supportFragmentManager).show()
    }

}