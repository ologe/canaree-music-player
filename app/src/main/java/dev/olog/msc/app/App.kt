package dev.olog.msc.app

import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import dev.olog.analytics.TrackerFacade
import dev.olog.appshortcuts.AppShortcuts
import dev.olog.core.interactor.SleepTimerUseCase
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.msc.tracker.ActivityAndFragmentsTracker
import io.alterac.blurkit.BlurKit
import javax.inject.Inject

@HiltAndroidApp
class App : ThemedApp() {

    private lateinit var appShortcuts: AppShortcuts

    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase

    @Inject
    lateinit var trackerFacade: TrackerFacade

    override fun onCreate() {
        super.onCreate()
        initializeComponents()
        initializeConstants()
        resetSleepTimer()

        registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallback())
        registerActivityLifecycleCallbacks(ActivityAndFragmentsTracker(trackerFacade))
    }

    private fun initializeComponents() {
        appShortcuts = AppShortcuts.instance(this)

        BlurKit.init(this)
        if (BuildConfig.DEBUG) {
//            Stetho.initializeWithDefaults(this)
        }
    }

    private fun initializeConstants() {
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)
    }

    private fun resetSleepTimer() {
        sleepTimerUseCase.reset()
    }

}
