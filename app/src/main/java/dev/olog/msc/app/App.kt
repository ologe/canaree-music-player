package dev.olog.msc.app

import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.content.BroadcastReceiver
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasBroadcastReceiverInjector
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.appshortcuts.AppShortcuts
import dev.olog.injection.CoreComponent
import dev.olog.msc.domain.interactor.prefs.SleepTimerUseCase
import dev.olog.msc.utils.PendingIntents
import dev.olog.presentation.AppConstants
import dev.olog.presentation.theme.*
import io.alterac.blurkit.BlurKit
import javax.inject.Inject

class App : Application(), Application.ActivityLifecycleCallbacks, HasBroadcastReceiverInjector {

    @Inject
    internal lateinit var broadcastInjector: DispatchingAndroidInjector<BroadcastReceiver>

    private lateinit var appShortcuts: AppShortcuts

    @Inject
    lateinit var darkModeListener: DarkModeListener

    @Inject
    lateinit var playerAppearanceListener: PlayerAppearanceListener

    @Inject
    lateinit var immersiveModeListener: ImmersiveModeListener

    @Inject
    lateinit var imageShapeListener: ImageShapeListener

    @Inject
    lateinit var quickActionListener: QuickActionListener

    @Inject
    lateinit var alarmManager: AlarmManager

    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        inject()
        initializeComponents()
        initializeConstants()
        resetSleepTimer()

        registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallback())
        registerActivityLifecycleCallbacks(this)
    }

    private fun initializeComponents() {
        appShortcuts = AppShortcuts.instance(this)

        BlurKit.init(this)
        if (BuildConfig.DEBUG) {
//            Traceur.enableLogging()
//            LeakCanary.install(this)
//            Stetho.initializeWithDefaults(this)
//            StrictMode.initialize()
        }
    }

    private fun initializeConstants() {
        AppConstants.initialize(this)
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)
    }

    private fun resetSleepTimer() {
        sleepTimerUseCase.reset()
        alarmManager.cancel(PendingIntents.stopMusicServiceIntent(this))
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
        darkModeListener.setCurrentActivity(activity)
        immersiveModeListener.setCurrentActivity(activity)

    }

    private fun inject() {
        DaggerAppComponent.factory()
            .create(CoreComponent.coreComponent(this))
            .inject(this)
    }

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> = broadcastInjector
}
