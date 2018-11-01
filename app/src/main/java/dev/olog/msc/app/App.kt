package dev.olog.msc.app

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceManager
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dev.olog.msc.BuildConfig
import dev.olog.msc.Permissions
import dev.olog.msc.R
import dev.olog.msc.app.shortcuts.AppShortcuts
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.domain.gateway.PodcastGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.domain.interactor.prefs.SleepTimerUseCase
import dev.olog.msc.presentation.image.creation.ImagesCreator
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.traceur.Traceur
import dev.olog.msc.updatePermissionValve
import dev.olog.msc.utils.PendingIntents
import io.alterac.blurkit.BlurKit
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
    @Inject lateinit var songGateway: SongGateway
    @Inject lateinit var podcastGateway: PodcastGateway
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
        updatePermissionValve(this, Permissions.canReadStorage(this))
    }

    override fun onStop(owner: LifecycleOwner) {
        updatePermissionValve(this, false)
    }

    private fun initRxMainScheduler() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            AndroidSchedulers.from(Looper.getMainLooper(), true)
        }
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
        return DaggerAppComponent.builder().create(this)
    }
}
