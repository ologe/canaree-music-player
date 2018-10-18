package dev.olog.msc.presentation.about.di

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.ActivityContext
import dev.olog.msc.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.dagger.scope.PerActivity
import dev.olog.msc.presentation.about.AboutActivity
import dev.olog.msc.pro.BillingImpl
import dev.olog.msc.pro.IBilling

@Module
class AboutActivityModule(
        private val activity: AboutActivity
) {

    @Provides
    @ActivityLifecycle
    fun provideLifecycle() : Lifecycle = activity.lifecycle

    @Provides
    fun provideActivity(): AppCompatActivity = activity

    @Provides
    @ActivityContext
    fun provideContext(): Context = activity

    @Provides
    @PerActivity
    fun provideBilling(impl: BillingImpl): IBilling {
        return impl
    }

}