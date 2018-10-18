package dev.olog.msc.presentation.splash.di

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.ActivityContext
import dev.olog.msc.presentation.splash.SplashActivity

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
    fun provideFragmentManager(): androidx.fragment.app.FragmentManager {
        return activity.supportFragmentManager
    }

}