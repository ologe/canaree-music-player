package dev.olog.msc.app

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dev.olog.msc.api.last.fm.LastFmModule
import dev.olog.msc.app.shortcuts.AppShortcutsModule
import dev.olog.msc.data.RepositoryHelperModule
import dev.olog.msc.data.RepositoryModule
import dev.olog.msc.data.prefs.PreferenceModule
import dev.olog.msc.floating.window.service.di.FloatingInfoServiceInjector
import dev.olog.msc.music.service.di.MusicServiceInjector
import dev.olog.msc.presentation.about.di.AboutActivityInjector
import dev.olog.msc.presentation.app.widget.WidgetBindingModule
import dev.olog.msc.presentation.main.di.MainActivityInjector
import dev.olog.msc.presentation.preferences.di.PreferencesActivityInjector
import dev.olog.msc.presentation.splash.di.SplashActivityInjector
import javax.inject.Singleton

@Component(modules = arrayOf(
        AppModule::class,
        SchedulersModule::class,
        AppShortcutsModule::class,
        LastFmModule::class,

//        // data
        RepositoryModule::class,
        RepositoryHelperModule::class,
        PreferenceModule::class,
//
//        // presentation
        AndroidBindingModule::class,
        WidgetBindingModule::class,
        AndroidSupportInjectionModule::class,
        SplashActivityInjector::class,
        MainActivityInjector::class,
        AboutActivityInjector::class,
        PreferencesActivityInjector::class,

//        // music service
        MusicServiceInjector::class,
        EqualizerModule::class,

//        // floating info service
        FloatingInfoServiceInjector::class
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