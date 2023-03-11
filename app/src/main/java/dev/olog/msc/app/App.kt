package dev.olog.msc.app

import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import dev.olog.core.AppInitializer
import dev.olog.core.interactor.SleepTimerUseCase
import dev.olog.msc.R
import io.alterac.blurkit.BlurKit
import javax.inject.Inject

@HiltAndroidApp
class App : ThemedApp() {

    @Inject
    lateinit var initializers: Set<@JvmSuppressWildcards AppInitializer>

    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase

    override fun onCreate() {
        super.onCreate()
        initializeComponents()
        initializeConstants()
        resetSleepTimer()

        registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallback())
    }

    private fun initializeComponents() {
        initializers.forEach { it.init() }

        BlurKit.init(this)
    }

    private fun initializeConstants() {
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)
    }

    private fun resetSleepTimer() {
        sleepTimerUseCase.reset()
    }

}
