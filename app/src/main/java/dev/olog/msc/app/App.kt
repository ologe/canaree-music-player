package dev.olog.msc.app

import android.app.AlarmManager
import android.content.Context
import android.os.StrictMode
import android.preference.PreferenceManager
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dev.olog.msc.AppShortcuts
import dev.olog.msc.BuildConfig
import dev.olog.msc.FirebaseAnalytics
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.floating.window.service.FloatingWindowService
import dev.olog.msc.music.service.MusicService
import dev.olog.msc.presentation.dialog.sleep.timer.SleepTimerDialog
import dev.olog.msc.presentation.library.tab.TabFragmentViewModel
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.presentation.mini.player.MiniPlayerFragment
import dev.olog.msc.presentation.mini.player.MiniPlayerFragmentPresenter
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.player.PlayerFragment
import dev.olog.msc.presentation.player.PlayerFragmentViewModel
import dev.olog.msc.presentation.search.SearchFragment
import dev.olog.msc.presentation.search.SearchFragmentViewModel
import dev.olog.msc.utils.PendingIntents
import dev.olog.msc.utils.img.CoverUtils


class App : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)

        AppConstants.initialize(this)
        CoverUtils.initialize()
        FirebaseAnalytics.initialize(this)

        resetSleepTimer()

        if (BuildConfig.DEBUG) {
//            initStrictMode()
//            LeakCanary.install(this)
//            initRxJavaDebug()
        }

        AppShortcuts.setup(this)
    }

    private fun resetSleepTimer(){
        SleepTimerDialog.resetTimer(this)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(PendingIntents.stopServiceIntent(this))
    }

    private fun initRxJavaDebug(){
//        RxJava2Debug.enableRxJava2AssemblyTracking(arrayOf(
//                "dev.olog.msc",
//                "dev.olog.data",
//                "dev.olog.domain",
//                "dev.olog.floating_info",
//                "dev.olog.music_service",
//                "dev.olog.presentation",
//                "dev.olog.shared",
//                "dev.olog.shared_android"
//        ))
    }

    private fun initStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())

        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
//                .penaltyDeath()
                .setClassInstanceLimit(MainActivity::class.java, 1)

                .setClassInstanceLimit(MusicService::class.java, 1)
                .setClassInstanceLimit(FloatingWindowService::class.java, 1)

                .setClassInstanceLimit(TabFragmentViewModel::class.java, 1)

                .setClassInstanceLimit(PlayerFragment::class.java, 1)
                .setClassInstanceLimit(PlayerFragmentViewModel::class.java, 1)

                .setClassInstanceLimit(MiniPlayerFragment::class.java, 1)
                .setClassInstanceLimit(MiniPlayerFragmentPresenter::class.java, 1)

                .setClassInstanceLimit(SearchFragment::class.java, 1)
                .setClassInstanceLimit(SearchFragmentViewModel::class.java, 1)

                .setClassInstanceLimit(Navigator::class.java, 1)
                .build())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerAppComponent.builder().create(this)
    }

}
