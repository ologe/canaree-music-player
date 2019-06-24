package dev.olog.msc.app

import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.msc.app.shortcuts.AppShortcuts
import dev.olog.msc.domain.interactor.prefs.SleepTimerUseCase
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.traceur.Traceur
import dev.olog.msc.utils.PendingIntents
import dev.olog.presentation.AppConstants
import dev.olog.presentation.theme.DarkMode
import io.alterac.blurkit.BlurKit
import javax.inject.Inject

class App : DaggerApplication(), Application.ActivityLifecycleCallbacks {

    @Suppress("unused")
    @Inject
    lateinit var appShortcuts: AppShortcuts
    @Inject
    lateinit var darkMode: DarkMode
    @Inject
    lateinit var alarmManager: AlarmManager
    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        initializeComponents()
        initializeConstants()
        resetSleepTimer()

        registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallback())
        registerActivityLifecycleCallbacks(this)
    }

    private fun initializeComponents() {
        BlurKit.init(this)
        if (BuildConfig.DEBUG) {
            Traceur.enableLogging()
//            LeakCanary.install(this)
//            Stetho.initializeWithDefaults(this)
//            StrictMode.initialize()
        }
    }

    private fun initializeConstants() {
        AppConstants.initialize(this)
        AppTheme.initialize(this)
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)
    }

    private fun resetSleepTimer() {
        sleepTimerUseCase.reset()
        alarmManager.cancel(PendingIntents.stopMusicServiceIntent(this))
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return CoreComponent.coreComponent(this)
    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityResumed(activity: Activity) {
        darkMode.updateCurrentActivity(activity)
    }
}
