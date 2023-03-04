package dev.olog.msc.app

import androidx.preference.PreferenceManager
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dev.olog.appshortcuts.AppShortcuts
import dev.olog.core.interactor.SleepTimerUseCase
import dev.olog.injection.CoreComponent
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import io.alterac.blurkit.BlurKit
import javax.inject.Inject

class App : ThemedApp(), HasAndroidInjector {

    private lateinit var appShortcuts: AppShortcuts

    @Inject
    internal lateinit var androidInjector: DispatchingAndroidInjector<Any>


    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase

    override fun onCreate() {
        super.onCreate()
        inject()
        initializeComponents()
        initializeConstants()
        resetSleepTimer()

        registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallback())
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

    private fun inject() {
        DaggerAppComponent.factory()
            .create(CoreComponent.coreComponent(this))
            .inject(this)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}
