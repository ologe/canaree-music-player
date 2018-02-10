package dev.olog.msc

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dev.olog.msc.dagger.AndroidBindingModule
import dev.olog.msc.data.RepositoryHelperModule
import dev.olog.msc.data.RepositoryModule
import dev.olog.msc.data.prefs.PreferenceModule
import dev.olog.msc.floating.window.di.FloatingInfoServiceInjector
import dev.olog.msc.module.AppModule
import dev.olog.msc.module.EqualizerModule
import dev.olog.msc.module.SchedulersModule
import dev.olog.msc.module.SharedClassModule
import dev.olog.msc.music.service.di.MusicServiceInjector
import dev.olog.msc.presentation.about.di.AboutActivityInjector
import dev.olog.msc.presentation.main.di.MainActivityInjector
import dev.olog.msc.presentation.neural.network.di.NeuralNetworkActivityInjector
import dev.olog.msc.presentation.preferences.di.PreferencesActivityInjector
import dev.olog.msc.presentation.shortcuts.di.ShortcutsActivityInjector
import dev.olog.msc.presentation.splash.di.SplashActivityInjector
import javax.inject.Singleton

@Component(modules = arrayOf(
        AppModule::class,
        SchedulersModule::class,
        AndroidBindingModule::class,

//        // data
        RepositoryModule::class,
        RepositoryHelperModule::class,
        PreferenceModule::class,
//
//        // presentation
        AndroidSupportInjectionModule::class,
        SplashActivityInjector::class,
        MainActivityInjector::class,
        AboutActivityInjector::class,
        ShortcutsActivityInjector::class,
        PreferencesActivityInjector::class,
        NeuralNetworkActivityInjector::class,

//        // music service
        MusicServiceInjector::class,
        EqualizerModule::class,

//        // floating info service
        FloatingInfoServiceInjector::class,
        SharedClassModule::class
))
@Singleton
interface AppComponent: AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>() {

        internal abstract fun module(module: AppModule): Builder

        override fun seedInstance(instance: App) {
            module(AppModule(instance))
        }

    }

}