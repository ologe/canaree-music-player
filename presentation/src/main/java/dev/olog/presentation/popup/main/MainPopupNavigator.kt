package dev.olog.presentation.popup.main

import androidx.fragment.app.FragmentActivity
import dev.olog.presentation.navigator.allowed
import dev.olog.presentation.sleeptimer.SleepTimerPickerDialogBuilder
import dev.olog.shared.mandatory
import javax.inject.Inject

internal class MainPopupNavigator @Inject constructor(
    private val activity: FragmentActivity
) {


    fun toSleepTimer() {
        mandatory(allowed()) ?: return

        SleepTimerPickerDialogBuilder(activity, activity.supportFragmentManager).show()
    }

}