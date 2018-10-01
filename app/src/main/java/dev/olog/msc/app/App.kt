package dev.olog.msc.app

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.os.Looper
import android.support.v7.preference.PreferenceManager
import com.tspoon.traceur.Traceur
import dagger.android.AndroidInjector
import dev.olog.msc.BuildConfig
import dev.olog.msc.Permissions
import dev.olog.msc.R
import dev.olog.msc.app.shortcuts.AppShortcuts
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.domain.interactor.prefs.SleepTimerUseCase
import dev.olog.msc.presentation.image.creation.ImagesCreator
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.updatePermissionValve
import dev.olog.msc.utils.PendingIntents
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


@SuppressLint("StaticFieldLeak")
lateinit var app: Context


class App : BaseApp() {

    @Suppress("unused") @Inject lateinit var appShortcuts: AppShortcuts
    @Suppress("unused") @Inject lateinit var imagesCreator: ImagesCreator
    @Suppress("unused") @Inject lateinit var keepDataAlive: KeepDataAlive

    @Inject lateinit var lastFmGateway: LastFmGateway
    @Inject lateinit var alarmManager: AlarmManager
    @Inject lateinit var sleepTimerUseCase: SleepTimerUseCase

    override fun initializeApp() {
        initializeComponents()
        initRxMainScheduler()
        initializeConstants()
        resetSleepTimer()

        registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallback())
    }

    override fun onStart(owner: LifecycleOwner) {
        updatePermissionValve(Permissions.canReadStorage(this))
    }

    override fun onStop(owner: LifecycleOwner) {
        updatePermissionValve(false)
    }

    private fun initRxMainScheduler() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            AndroidSchedulers.from(Looper.getMainLooper(), true)
        }
    }

    private fun initializeComponents() {
        Traceur.enableLogging()


//        LeakCanary.install(this)
        if (BuildConfig.DEBUG) {
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

    override fun applicationInjector(): AndroidInjector<out Application> {
        return DaggerAppComponent.builder().create(this)
    }
}
