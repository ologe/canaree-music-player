package dev.olog.msc.app

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.support.v7.preference.PreferenceManager
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.msc.app.shortcuts.AppShortcuts
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.domain.interactor.prefs.SleepTimerUseCase
import dev.olog.msc.presentation.image.creation.ImagesCreator
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.PendingIntents
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
lateinit var app: Context

class App : DaggerApplication() {

    @Suppress("unused") @Inject lateinit var appShortcuts: AppShortcuts
    @Suppress("unused") @Inject lateinit var imagesCreator: ImagesCreator
    @Suppress("unused") @Inject lateinit var keepDataAlive: KeepDataAlive

    @Inject lateinit var lastFmGateway: LastFmGateway
    @Inject lateinit var alarmManager: AlarmManager
    @Inject lateinit var sleepTimerUseCase: SleepTimerUseCase

    override fun onCreate() {
        super.onCreate()
        app = this

        initializeDebug()
        initializeConstants()
        resetSleepTimer()

        registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallback())
    }

    private fun initializeDebug(){
        if (BuildConfig.DEBUG){
//            Stetho.initializeWithDefaults(this)
//            LeakCanary.install(this)
//            StrictMode.initialize()
//            Traceur.enableLogging()
        }
    }

    private fun initializeConstants(){
        AppConstants.initialize(this)
        AppTheme.initialize(this)
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)
    }

    private fun resetSleepTimer(){
        sleepTimerUseCase.reset()
        alarmManager.cancel(PendingIntents.stopMusicServiceIntent(this))
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerAppComponent.builder().create(this)
    }

}
