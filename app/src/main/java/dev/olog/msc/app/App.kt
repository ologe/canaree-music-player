package dev.olog.msc.app

import android.app.AlarmManager
import android.content.Context
import android.preference.PreferenceManager
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.dialog.sleep.timer.SleepTimerDialog
import dev.olog.msc.utils.PendingIntents
import dev.olog.msc.utils.img.CoverUtils
import javax.inject.Inject


class App : DaggerApplication() {

    @Inject lateinit var strictMode: StrictMode
    @Inject lateinit var appShortcuts: AppShortcuts

    override fun onCreate() {
        super.onCreate()
        strictMode.initialize()

        if (BuildConfig.DEBUG) {
//            LeakCanary.install(this)
//            initRxJavaDebug()
        }

        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)

        AppConstants.initialize(this)
        CoverUtils.initialize()

        resetSleepTimer()
    }

    private fun resetSleepTimer(){
        SleepTimerDialog.resetTimer(this)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(PendingIntents.stopServiceIntent(this))
    }

    private fun initRxJavaDebug(){
//        RxJava2Debug.enableRxJava2AssemblyTracking(arrayOf("dev.olog.msc"))
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerAppComponent.builder().create(this)
    }

}
