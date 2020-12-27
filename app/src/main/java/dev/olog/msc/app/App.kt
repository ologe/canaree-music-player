package dev.olog.msc.app

import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import dev.olog.core.AppShortcuts
import dev.olog.core.interactor.SleepTimerUseCase
import dev.olog.msc.R
import dev.olog.navigation.internal.ActivityProvider
import io.alterac.blurkit.BlurKit
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : ThemedApp() {

    @Inject
    lateinit var activityProvider: ActivityProvider

    @Inject
    lateinit var appShortcuts: AppShortcuts

    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        initializeComponents()
        initializeConstants()
        resetSleepTimer()

        registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallback())
    }

    private fun initializeComponents() {
        appShortcuts.initialize()

        BlurKit.init(this)
    }

    private fun initializeConstants() {
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)
    }

    private fun resetSleepTimer() {
        sleepTimerUseCase.reset()
    }

}
