package dev.olog.presentation.activity_splash.di

import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.presentation.activity_splash.SplashActivity

@Module
class SplashActivityModule(
        private val activity: SplashActivity
) {

    @Provides
    fun provideActivity(): AppCompatActivity {
        return activity
    }

}