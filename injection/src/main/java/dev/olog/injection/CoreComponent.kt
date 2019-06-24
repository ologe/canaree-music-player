package dev.olog.injection

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import dagger.BindsInstance
import dagger.Component
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.executor.ComputationScheduler
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.*
import dev.olog.core.prefs.*
import dev.olog.data.DataModule
import dev.olog.data.PreferenceModule
import dev.olog.data.RepositoryHelperModule
import dev.olog.data.RepositoryModule
import dev.olog.data.api.lastfm.LastFmModule
import dev.olog.injection.equalizer.EqualizerModule
import dev.olog.injection.equalizer.IBassBoost
import dev.olog.injection.equalizer.IEqualizer
import dev.olog.injection.equalizer.IVirtualizer
import java.text.Collator
import javax.inject.Singleton

@Component(
    modules = arrayOf(
        CoreModule::class,
        SchedulersModule::class,
        LastFmModule::class,

//        // data
        RepositoryModule::class,
        RepositoryHelperModule::class,
        PreferenceModule::class,
        DataModule::class,
        EqualizerModule::class
//
//        // presentation
//        ActivityBindingsModule ::class,
//        AboutActivityInjector::class,
//        PreferencesActivityInjector::class,
//        PlaylistChooserActivityInjector::class

    )
)
@Singleton
interface CoreComponent {

    fun provideAlarmManager(): AlarmManager

    @ApplicationContext
    fun context(): Context
    fun resources(): Resources

    fun lastFmGateway(): LastFmGateway2

    fun prefs(): AppPreferencesGateway
    fun musicPrefs(): MusicPreferencesGateway
    fun tutorialPrefs(): TutorialPreferenceGateway
    fun equalizerPrefs(): EqualizerPreferencesGateway
    fun sortPrefs(): SortPreferences
    fun blacklistPrefs(): BlacklistPreferences

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