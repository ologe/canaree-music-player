package dev.olog.presentation.activity_splash.di

import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.Module
import dagger.Provides
import dev.olog.presentation.activity_splash.SplashActivity

@Module
class SplashActivityModule(
        private val activity: SplashActivity
) {

    @Provides
    fun provideActivity(): AppCompatActivity = activity

    @Provides
    fun provideRxPermission() : RxPermissions = RxPermissions(activity)

    @Provides
    fun provideFragmentManager() : FragmentManager {
        return activity.supportFragmentManager
    }

}