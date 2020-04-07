package dev.olog.injection

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import dagger.BindsInstance
import dagger.Component
import dev.olog.analytics.AnalyticsModule
import dev.olog.analytics.TrackerFacade
import dev.olog.domain.IEncrypter
import dev.olog.domain.gateway.*
import dev.olog.domain.gateway.podcast.PodcastAuthorGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.spotify.SpotifyGateway
import dev.olog.domain.gateway.track.*
import dev.olog.domain.prefs.*
import dev.olog.domain.schedulers.Schedulers
import dev.olog.data.di.*
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
        ServiceModule::class,
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
    fun trackGateway(): TrackGateway
    fun albumGateway(): AlbumGateway
    fun artistGateway(): ArtistGateway
    fun genreGateway(): GenreGateway
    fun podcastPlaylistGateway(): PodcastPlaylistGateway
    fun podcastGateway(): PodcastGateway
    fun podcastArtistGateway(): PodcastAuthorGateway

    fun equalizerGateway(): EqualizerGateway

    fun spotifyGateway(): SpotifyGateway

    fun encrypter(): IEncrypter

    fun trackerFacade(): TrackerFacade

    fun schedulers(): Schedulers

    fun provideAlarmService(): AlarmService

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