package dev.olog.msc.app

import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import dev.olog.analytics.TrackerFacade
import dev.olog.core.AppShortcuts
import dev.olog.core.interactor.SleepTimerUseCase
import dev.olog.msc.R
import dev.olog.msc.tracker.ActivityAndFragmentsTracker
import io.alterac.blurkit.BlurKit
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : ThemedApp() {

    @Inject
    lateinit var appShortcuts: AppShortcuts

    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase

    @Inject
    lateinit var trackerFacade: TrackerFacade

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        initializeComponents()
        initializeConstants()
        resetSleepTimer()

        registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallback())
        registerActivityLifecycleCallbacks(ActivityAndFragmentsTracker(trackerFacade))
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
