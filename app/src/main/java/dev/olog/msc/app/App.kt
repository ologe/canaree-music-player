package dev.olog.msc.app

import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import dev.olog.appshortcuts.AppShortcuts
import dev.olog.core.interactor.SleepTimerUseCase
import dev.olog.msc.R
import javax.inject.Inject

@HiltAndroidApp
class App : ThemedApp() {

    private lateinit var appShortcuts: AppShortcuts

    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase

    override fun onCreate() {
        super.onCreate()
        initializeComponents()
        initializeConstants()
        resetSleepTimer()
    }

    private fun initializeComponents() {
        appShortcuts = AppShortcuts.instance(this)
    }

    private fun initializeConstants() {
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)
    }

    private fun resetSleepTimer() {
        sleepTimerUseCase.reset()
    }

}
