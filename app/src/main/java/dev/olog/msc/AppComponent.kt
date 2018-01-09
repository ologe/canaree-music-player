package dev.olog.msc

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dev.olog.data.RepositoryHelperModule
import dev.olog.data.RepositoryModule
import dev.olog.data.preferences.PreferenceModule
import dev.olog.floating_info.di.FloatingInfoServiceInjector
import dev.olog.music_service.di.MusicServiceInjector
import dev.olog.presentation.activity_about.di.AboutActivityInjector
import dev.olog.presentation.activity_main.di.MainActivityInjector
import dev.olog.presentation.activity_preferences.di.PreferencesActivityInjector
import dev.olog.presentation.activity_shortcuts.di.ShortcutsActivityInjector
import dev.olog.presentation.activity_splash.di.SplashActivityInjector
import javax.inject.Singleton

@Component(modules = arrayOf(
        AppModule::class,
        SchedulersModule::class,

        // data
        RepositoryModule::class,
        RepositoryHelperModule::class,
        PreferenceModule::class,

        // presentation
        AndroidSupportInjectionModule::class,
        SplashActivityInjector::class,
        MainActivityInjector::class,
        AboutActivityInjector::class,
        ShortcutsActivityInjector::class,
        PreferencesActivityInjector::class,

        // music service
        MusicServiceInjector::class,

        // floating info service
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