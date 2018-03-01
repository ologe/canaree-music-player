package dev.olog.msc.presentation.main.di

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.ActivityContext
import dev.olog.msc.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.interfaces.pro.BillingImpl
import dev.olog.msc.interfaces.pro.IBilling
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.main.MainActivity

@Module
class MainActivityModule(
        private val activity: MainActivity
) {

    @Provides
    fun provideInstance() = activity

    @Provides
    @ActivityContext
    internal fun provideContext(): Context = activity

    @Provides
    @ActivityLifecycle
    internal fun provideLifecycle(): Lifecycle = activity.lifecycle

    @Provides
    internal fun provideActivity(): Activity = activity

    @Provides
    internal fun provideSupportActivity(): AppCompatActivity = activity

    @Provides
    internal fun provideFragmentActivity(): FragmentActivity = activity

    @Provides
    internal fun provideMusicGlue(): MediaProvider = activity

    @Provides
    internal fun provideBilling(impl: BillingImpl): IBilling = impl

}