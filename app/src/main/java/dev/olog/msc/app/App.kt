package dev.olog.msc.app

import android.app.AlarmManager
import android.content.Context
import android.preference.PreferenceManager
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import com.tspoon.traceur.Traceur
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.msc.app.shortcuts.AppShortcuts
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.dialog.sleep.timer.SleepTimerDialog
import dev.olog.msc.presentation.image.creation.ImagesCreator
import dev.olog.msc.pro.Validation
import dev.olog.msc.utils.PendingIntents
import javax.inject.Inject


class App : DaggerApplication() {

    @Suppress("unused") @Inject lateinit var appShortcuts: AppShortcuts
    @Suppress("unused") @Inject lateinit var imagesCreator: ImagesCreator
    @Suppress("unused") @Inject lateinit var keepDataAlive: KeepDataAlive
    @Inject lateinit var validation: Validation

    override fun onCreate() {
        super.onCreate()

        initializeDebug()
        initializeConstants()
        resetSleepTimer()

//        validation.isValid().subscribe({}, Throwable::printStackTrace)
    }

    private fun initializeDebug(){
        if (BuildConfig.DEBUG){
            Stetho.initializeWithDefaults(this)
            LeakCanary.install(this)
            StrictMode.initialize()
            Traceur.enableLogging()
        }
    }

    private fun initializeConstants(){
        AppConstants.initialize(this)
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)
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
