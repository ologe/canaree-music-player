package dev.olog.msc.app

import android.app.AlarmManager
import android.content.Context
import android.preference.PreferenceManager
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.msc.app.shortcuts.AppShortcuts
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.dialog.sleep.timer.SleepTimerDialog
import dev.olog.msc.presentation.image.creation.ImagesCreator
import dev.olog.msc.utils.PendingIntents
import dev.olog.msc.utils.img.CoverUtils
import javax.inject.Inject


class App : DaggerApplication() {

    @Suppress("unused") @Inject lateinit var appShortcuts: AppShortcuts
    @Suppress("unused") @Inject lateinit var imagesCreator: ImagesCreator

    override fun onCreate() {
        super.onCreate()

//        initializeDebug()
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)
        initializeConstants()
        resetSleepTimer()
    }

    private fun initializeDebug(){
        if (BuildConfig.DEBUG){
            LeakCanary.install(this)
            StrictMode.initialize()
//            RxJava2Debug.enableRxJava2AssemblyTracking(arrayOf("dev.olog.msc"))
        }
    }

    private fun initializeConstants(){
        AppConstants.initialize(this)
        CoverUtils.initialize()
    }

    private fun resetSleepTimer(){
        SleepTimerDialog.resetTimer(this)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(PendingIntents.stopServiceIntent(this))
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerAppComponent.builder().create(this)
    }

}
