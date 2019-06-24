package dev.olog.msc.app

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.executor.ComputationScheduler
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.*
import dev.olog.data.DataModule
import dev.olog.msc.api.last.fm.LastFmModule
import dev.olog.msc.app.shortcuts.AppShortcuts
import dev.olog.msc.app.shortcuts.AppShortcutsModule
import dev.olog.msc.data.RepositoryHelperModule
import dev.olog.msc.data.RepositoryModule
import dev.olog.msc.data.prefs.PreferenceModule
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.domain.gateway.OfflineLyricsGateway
import dev.olog.msc.domain.gateway.RecentSearchesGateway
import dev.olog.msc.domain.gateway.UsedImageGateway
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.gateway.prefs.EqualizerPreferencesGateway
import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.domain.gateway.prefs.TutorialPreferenceGateway
import dev.olog.msc.domain.interactor.last.fm.scrobble.LastFmEncrypter
import dev.olog.msc.music.service.equalizer.IBassBoost
import dev.olog.msc.music.service.equalizer.IEqualizer
import dev.olog.msc.music.service.equalizer.IVirtualizer
import dev.olog.msc.presentation.ViewModelModule
import dev.olog.msc.presentation.about.di.AboutActivityInjector
import dev.olog.msc.presentation.app.widget.WidgetBindingModule
import dev.olog.msc.presentation.app.widget.WidgetClasses
import dev.olog.msc.presentation.main.di.MainActivityInjector
import dev.olog.msc.presentation.preferences.di.PreferencesActivityInjector
import dev.olog.msc.presentation.shortcuts.playlist.chooser.di.PlaylistChooserActivityInjector
import java.text.Collator
import javax.inject.Singleton

@Component(
    modules = arrayOf(
        CoreModule::class,
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

        EqualizerModule::class
    )
)
@Singleton
interface CoreComponent : AndroidInjector<App> {

    fun provideAlarmManager(): AlarmManager

    @ApplicationContext
    fun context(): Context

    fun prefs(): AppPreferencesGateway
    fun musicPrefs(): MusicPreferencesGateway
    fun tutorialPrefs(): TutorialPreferenceGateway
    fun equalizerPrefs(): EqualizerPreferencesGateway

    fun lastFmGateway(): LastFmGateway
    fun usedImageGateway(): UsedImageGateway
    fun playingQueueGateway(): PlayingQueueGateway
    fun favoriteGateway(): FavoriteGateway
    fun recentSearches(): RecentSearchesGateway
    fun offlineLyrics(): OfflineLyricsGateway

    fun sharedPreferences(): SharedPreferences

    fun equalizer(): IEqualizer
    fun virtualizer(): IVirtualizer
    fun bassBoost(): IBassBoost

    fun cpuDispatcher(): ComputationScheduler
    fun ioDispatcher(): IoScheduler
    fun encrypter(): LastFmEncrypter

    fun appShortcuts(): AppShortcuts
    fun widgetClasses(): WidgetClasses
    fun collator(): Collator

    fun folderGateway2(): FolderGateway2
    fun playlistGateway2(): PlaylistGateway2
    fun songGateway2(): SongGateway2
    fun albumGateway2(): AlbumGateway2
    fun artistGateway2(): ArtistGateway2
    fun genreGateway2(): GenreGateway2
    fun podcastPlaylistGateway2(): PodcastPlaylistGateway2
    fun podcastGateway2(): PodcastGateway2
    fun podcastAlbumGateway2(): PodcastAlbumGateway2
    fun podcastArtistGateway2(): PodcastArtistGateway2

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance instance: Application): CoreComponent
    }

    companion object {
        private var component: CoreComponent? = null

        fun coreComponent(application: Application): CoreComponent {
            if (component == null){
                component = DaggerCoreComponent.factory().create(application)
            }
            return component!!
        }
    }

}