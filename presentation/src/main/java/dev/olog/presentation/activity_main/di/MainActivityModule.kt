package dev.olog.presentation.activity_main.di

import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import dagger.Module
import dagger.Provides
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.dagger.ActivityContext
import dev.olog.presentation.dagger.PerActivity

@Module
class MainActivityModule(
        private val activity: MainActivity
) {

    @Provides
    @ActivityContext
    internal fun provideContext(): Context = activity

    @Provides
    internal fun provideFragmentManager(): FragmentManager {
        return activity.supportFragmentManager
    }

    @Provides
    @PerActivity
    internal fun provdeViewPool(): RecyclerView.RecycledViewPool {
        return RecyclerView.RecycledViewPool()
    }

    @Provides
    internal fun provideFragmentActivity() : FragmentActivity = activity

}