package dev.olog.msc.app

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.executor.ComputationScheduler
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.*
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.DataModule
import dev.olog.injection.CoreModule
import dev.olog.injection.SchedulersModule
import dev.olog.injection.WidgetClasses
import dev.olog.msc.api.last.fm.LastFmModule
import dev.olog.msc.app.shortcuts.AppShortcuts
import dev.olog.msc.app.shortcuts.AppShortcutsModule
import dev.olog.msc.data.RepositoryHelperModule
import dev.olog.msc.data.RepositoryModule
import dev.olog.msc.data.prefs.PreferenceModule
import dev.olog.msc.domain.gateway.prefs.*
import dev.olog.msc.domain.interactor.last.fm.scrobble.LastFmEncrypter
import dev.olog.msc.music.service.equalizer.IBassBoost
import dev.olog.msc.music.service.equalizer.IEqualizer
import dev.olog.msc.music.service.equalizer.IVirtualizer
import java.text.Collator
import javax.inject.Singleton

@Component(
    modules = arrayOf(
        CoreModule::class,
        SchedulersModule::class,
        LastFmModule::class,
        AndroidInjectionModule::class,

//        // data
        RepositoryModule::class,
        RepositoryHelperModule::class,
        PreferenceModule::class,
        DataModule::class,
        AppShortcutsModule::class,
        EqualizerModule::class
//
//        // presentation
//        ActivityBindingsModule ::class,
//        WidgetBindingModule::class,
//        AboutActivityInjector::class,
//        PreferencesActivityInjector::class,
//        PlaylistChooserActivityInjector::class

    )
)
@Singleton
interface CoreComponent : AndroidInjector<App> {

    fun provideAlarmManager(): AlarmManager

    @ApplicationContext
    fun context(): Context
    fun resources(): Resources

    fun prefs(): AppPreferencesGateway
    fun musicPrefs(): MusicPreferencesGateway
    fun tutorialPrefs(): TutorialPreferenceGateway
    fun equalizerPrefs(): EqualizerPreferencesGateway
    fun presentationPrefs(): PresentationPreferences
    fun sortPrefs(): SortPreferences
    fun blacklistPrefs(): BlacklistPreferences

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
            if (component == null) {
                component = DaggerCoreComponent.factory().create(application)
            }
            return component!!
        }
    }

}