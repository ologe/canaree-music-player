package dev.olog.msc.app

import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import dev.olog.core.AppInitializer
import dev.olog.feature.media.api.interactor.SleepTimerUseCase
import dev.olog.feature.shortcuts.api.AppShortcuts
import io.alterac.blurkit.BlurKit
import javax.inject.Inject

@HiltAndroidApp
class App : ThemedApp() {

    @Inject
    lateinit var appShortcuts: AppShortcuts

    @Inject
    lateinit var sleepTimerUseCase: SleepTimerUseCase
    @Inject
    lateinit var initializers: Set<@JvmSuppressWildcards AppInitializer>

    override fun onCreate() {
        super.onCreate()
        initializers.forEach(AppInitializer::init)
        initializeComponents()
        initializeConstants()
        resetSleepTimer()
    }

    private fun initializeComponents() {
        appShortcuts.setup()

        BlurKit.init(this)
    }

    private fun initializeConstants() {
        PreferenceManager.setDefaultValues(this, dev.olog.feature.settings.R.xml.prefs, false)
    }

    private fun resetSleepTimer() {
        sleepTimerUseCase.reset()
    }

}
