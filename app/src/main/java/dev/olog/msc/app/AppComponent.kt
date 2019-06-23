package dev.olog.msc.app

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dev.olog.data.DataModule
import dev.olog.msc.api.last.fm.LastFmModule
import dev.olog.msc.app.shortcuts.AppShortcutsModule
import dev.olog.msc.data.RepositoryHelperModule
import dev.olog.msc.data.RepositoryModule
import dev.olog.msc.data.prefs.PreferenceModule
import dev.olog.msc.floating.window.service.di.FloatingWindowServiceInjector
import dev.olog.msc.music.service.di.MusicServiceInjector
import dev.olog.msc.presentation.ViewModelModule
import dev.olog.msc.presentation.about.di.AboutActivityInjector
import dev.olog.msc.presentation.app.widget.WidgetBindingModule
import dev.olog.msc.presentation.main.di.MainActivityInjector
import dev.olog.msc.presentation.preferences.di.PreferencesActivityInjector
import dev.olog.msc.presentation.shortcuts.playlist.chooser.di.PlaylistChooserActivityInjector
import javax.inject.Singleton

@Component(
    modules = arrayOf(
        AppModule::class,
        SchedulersModule::class,
        AppShortcutsModule::class,
        LastFmModule::class,
        AndroidInjectionModule::class,

//        // data
        RepositoryModule::class,
        RepositoryHelperModule::class,
        PreferenceModule::class,
        DataModule::class,
//
//        // presentation
                ActivityBindingsModule ::class,
        WidgetBindingModule::class,
        MainActivityInjector::class,
        AboutActivityInjector::class,
        PreferencesActivityInjector::class,
        PlaylistChooserActivityInjector::class,
        ViewModelModule::class,

//        // music service
        MusicServiceInjector::class,
        EqualizerModule::class,

//        // floating info service
        FloatingWindowServiceInjector::class
    )
)
@Singleton
interface AppComponent : AndroidInjector<App> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance instance: Application): AppComponent
    }

    companion object {
        private var component: AppComponent? = null

        fun coreComponent(application: Application): AppComponent {
            if (component == null){
                component = DaggerAppComponent.factory().create(application)
            }
            return component!!
        }
    }

}