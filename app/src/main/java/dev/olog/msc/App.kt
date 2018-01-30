package dev.olog.msc

import android.app.AlarmManager
import android.content.Context
import android.os.StrictMode
import android.preference.PreferenceManager
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dev.olog.floating_info.FloatingInfoService
import dev.olog.music_service.MusicService
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.dialog_sleep_timer.SleepTimerDialog
import dev.olog.presentation.fragment_mini_player.MiniPlayerFragment
import dev.olog.presentation.fragment_mini_player.MiniPlayerFragmentViewModel
import dev.olog.presentation.fragment_player.PlayerFragment
import dev.olog.presentation.fragment_player.PlayerFragmentViewModel
import dev.olog.presentation.fragment_search.SearchFragment
import dev.olog.presentation.fragment_search.SearchFragmentViewModel
import dev.olog.presentation.fragment_tab.TabFragmentViewModel
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_floating_info.FloatingInfoServiceHelper
import dev.olog.shared_android.Constants
import dev.olog.shared_android.CoverUtils
import dev.olog.shared_android.PendingIntents
import dev.olog.shared_android.analitycs.FirebaseAnalytics
import dev.olog.shared_android.interfaces.FloatingInfoServiceClass
import javax.inject.Inject


class App : DaggerApplication() {

    @Inject lateinit var floatingInfoClass : FloatingInfoServiceClass

    override fun onCreate() {
        super.onCreate()

        FirebasePerformance.initialize()

        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)

        Constants.initialize(this)
        CoverUtils.initialize(this)
        FirebaseAnalytics.initialize(this)

        resetSleepTimer()

        if (BuildConfig.DEBUG) {
//            initStrictMode()
//            LeakCanary.install(this)
//            initRxJavaDebug()
        }

        AppShortcuts.setup(this)

        handleFloatingServiceStartOnLaunch()

    }

    private fun resetSleepTimer(){
        SleepTimerDialog.resetTimer(this)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(PendingIntents.stopServiceIntent(this, this::class.java))
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
                .setClassInstanceLimit(TabViewPagerAdapter::class.java, 1)

                .setClassInstanceLimit(MusicService::class.java, 1)
                .setClassInstanceLimit(FloatingInfoService::class.java, 1)

                .setClassInstanceLimit(TabFragmentViewModel::class.java, 1)

                .setClassInstanceLimit(PlayerFragment::class.java, 1)
                .setClassInstanceLimit(PlayerFragmentViewModel::class.java, 1)

                .setClassInstanceLimit(MiniPlayerFragment::class.java, 1)
                .setClassInstanceLimit(MiniPlayerFragmentViewModel::class.java, 1)

                .setClassInstanceLimit(SearchFragment::class.java, 1)
                .setClassInstanceLimit(SearchFragmentViewModel::class.java, 1)

                .setClassInstanceLimit(Navigator::class.java, 1)
                .build())
    }

    private fun handleFloatingServiceStartOnLaunch(){
        val canLaunch = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.prefs_start_floating_window_at_startup_key), false)
        if (canLaunch){
            FloatingInfoServiceHelper.startServiceIfHasOverlayPermission(this, floatingInfoClass)
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerAppComponent.builder().create(this)
    }

}
