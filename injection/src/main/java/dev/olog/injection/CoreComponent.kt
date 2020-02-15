package dev.olog.injection

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import dagger.BindsInstance
import dagger.Component
import dev.olog.analytics.AnalyticsModule
import dev.olog.analytics.TrackerFacade
import dev.olog.core.IEncrypter
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.gateway.*
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.core.prefs.*
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.di.DataModule
import dev.olog.data.di.PreferenceModule
import dev.olog.data.di.RepositoryHelperModule
import dev.olog.data.di.NetworkModule
import dev.olog.equalizer.EqualizerModule
import dev.olog.equalizer.bassboost.IBassBoost
import dev.olog.equalizer.equalizer.IEqualizer
import dev.olog.equalizer.virtualizer.IVirtualizer
import dev.olog.injection.schedulers.SchedulersModule
import javax.inject.Singleton

@Component(
    modules = arrayOf(
        CoreModule::class,
        SchedulersModule::class,
        NetworkModule::class,
        AnalyticsModule::class,

//        // data
        RepositoryHelperModule::class,
        PreferenceModule::class,
        DataModule::class,
        EqualizerModule::class
    )
)
@Singleton
interface CoreComponent {

    @ApplicationContext
    fun context(): Context
    fun resources(): Resources

    fun lastFmGateway(): ImageRetrieverGateway

    fun prefs(): AppPreferencesGateway
    fun musicPrefs(): MusicPreferencesGateway
    fun tutorialPrefs(): TutorialPreferenceGateway
    fun equalizerPrefs(): EqualizerPreferencesGateway
    fun sortPrefs(): SortPreferences
    fun blacklistPrefs(): BlacklistPreferences
    
    fun playingQueueGateway(): PlayingQueueGateway
    fun favoriteGateway(): FavoriteGateway
    fun recentSearches(): RecentSearchesGateway
    fun offlineLyrics(): OfflineLyricsGateway

    fun sharedPreferences(): SharedPreferences

    fun equalizer(): IEqualizer
    fun virtualizer(): IVirtualizer
    fun bassBoost(): IBassBoost

    fun folderGateway(): FolderGateway
    fun folderNavigatorGateway(): FolderNavigatorGateway
    fun playlistGateway(): PlaylistGateway
    fun songGateway(): SongGateway
    fun albumGateway(): AlbumGateway
    fun artistGateway(): ArtistGateway
    fun genreGateway(): GenreGateway
    fun podcastPlaylistGateway(): PodcastPlaylistGateway
    fun podcastGateway(): PodcastGateway
    fun podcastAlbumGateway(): PodcastAlbumGateway
    fun podcastArtistGateway(): PodcastArtistGateway

    fun equalizerGateway(): EqualizerGateway

    fun encrypter(): IEncrypter

    fun trackerFacade(): TrackerFacade

    fun schedulers(): Schedulers

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance instance: Application): CoreComponent
    }

    companion object {
        private var component: CoreComponent? = null

        @JvmStatic
        fun coreComponent(application: Application): CoreComponent {
            if (component == null) {
                component = DaggerCoreComponent.factory().create(application)
            }
            return component!!
        }
    }

}