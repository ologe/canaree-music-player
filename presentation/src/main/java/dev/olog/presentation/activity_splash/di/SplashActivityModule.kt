package dev.olog.presentation.activity_splash.di

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.Module
import dagger.Provides
import dev.olog.presentation.activity_splash.SplashActivity
import dev.olog.presentation.dagger.ActivityContext

@Module
class SplashActivityModule(
        private val activity: SplashActivity
) {

    @Provides
    @ActivityContext
    internal fun provideContext(): Context = activity

    @Provides
    fun provideActivity(): AppCompatActivity = activity

    @Provides
    fun provideRxPermission() : RxPermissions = RxPermissions(activity)

    @Provides
    fun provideFragmentManager(): FragmentManager {
        return activity.supportFragmentManager
    }

}