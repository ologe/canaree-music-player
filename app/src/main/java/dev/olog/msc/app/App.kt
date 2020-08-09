package dev.olog.msc.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.olog.lib.analytics.TrackerFacade
import dev.olog.feature.app.shortcuts.AppShortcuts
import dev.olog.domain.interactor.SleepTimerUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.msc.BuildConfig
import dev.olog.msc.debug.CrashlyticsLogTree
import dev.olog.msc.tracker.ActivityAndFragmentsTracker
import dev.olog.navigation.utils.ActivityProvider
import dev.olog.shared.android.theme.ThemeManager
import dev.olog.shared.android.theme.ThemeUtils.THEME_SERVICE
import io.alterac.blurkit.BlurKit
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    private lateinit var appShortcuts: AppShortcuts

    @Inject
    lateinit var activityProvider: ActivityProvider

    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase

    @Inject
    lateinit var trackerFacade: TrackerFacade

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var schedulers: Schedulers

    override fun onCreate() {
        super.onCreate()
        initializeTimber()
        initializeComponents()
        initializeConstants()
        resetSleepTimer()

        // TODO inject from a set?
        registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallback())
        registerActivityLifecycleCallbacks(ActivityAndFragmentsTracker(trackerFacade))
    }

    private fun initializeComponents() {
        appShortcuts = AppShortcuts.instance(this, schedulers)

        BlurKit.init(this)
    }

    private fun initializeConstants() {
        // TODO delegate via multibindigs
//        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)
    }

    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.plant(CrashlyticsLogTree())
    }

    private fun resetSleepTimer() {
        sleepTimerUseCase.reset()
    }

    override fun getSystemService(name: String): Any? {
        if (name == THEME_SERVICE) {
            return themeManager
        }
        return super.getSystemService(name)
    }
}
