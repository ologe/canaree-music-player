package dev.olog.msc.app

import android.app.Application
import androidx.preference.PreferenceManager
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dev.olog.analytics.TrackerFacade
import dev.olog.appshortcuts.AppShortcuts
import dev.olog.core.interactor.SleepTimerUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.injection.CoreComponent
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.msc.debug.CrashlyticsLogTree
import dev.olog.msc.tracker.ActivityAndFragmentsTracker
import dev.olog.shared.android.theme.ThemeManager
import dev.olog.shared.android.theme.ThemeUtils.THEME_SERVICE
import io.alterac.blurkit.BlurKit
import timber.log.Timber
import javax.inject.Inject

class App : Application(), HasAndroidInjector {

    private lateinit var appShortcuts: AppShortcuts

    @Inject
    internal lateinit var androidInjector: DispatchingAndroidInjector<Any>

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
        inject()
        initializeTimber()
        initializeComponents()
        initializeConstants()
        resetSleepTimer()

        registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallback())
        registerActivityLifecycleCallbacks(ActivityAndFragmentsTracker(trackerFacade))
    }

    private fun initializeComponents() {
        appShortcuts = AppShortcuts.instance(this, schedulers)

        BlurKit.init(this)
        if (BuildConfig.DEBUG) {
//            Stetho.initializeWithDefaults(this)
        }
    }

    private fun initializeConstants() {
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)
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

    private fun inject() {
        DaggerAppComponent.factory()
            .create(CoreComponent.coreComponent(this))
            .inject(this)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun getSystemService(name: String): Any? {
        if (name == THEME_SERVICE) {
            return themeManager
        }
        return super.getSystemService(name)
    }
}
