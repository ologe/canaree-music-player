package dev.olog.msc.presentation.splash.di

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.splash.SplashActivity

@Module(subcomponents = arrayOf(SplashActivitySubComponent::class))
abstract class SplashActivityInjector {

    @Binds
    @IntoMap
    @ActivityKey(SplashActivity::class)
    internal abstract fun injectorFactory(builder: SplashActivitySubComponent.Builder)
            : AndroidInjector.Factory<out Activity>

}