package dev.olog.data.di

import android.content.Context
import android.provider.MediaStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.domain.prefs.SortPreferences
import dev.olog.data.di.qualifier.Podcast
import dev.olog.data.di.qualifier.Tracks
import dev.olog.data.queries.TrackQueries

@Module
@InstallIn(ApplicationComponent::class)
class QueriesModule {

    @Tracks
    @Provides
    internal fun provideTracksQueries(
        @ApplicationContext context: Context,
        blacklistPrefs: BlacklistPreferences,
        sortPrefs: SortPreferences
    ): TrackQueries {
        return TrackQueries(
            context.contentResolver,
            blacklistPrefs,
            sortPrefs,
            false,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    @Podcast
    @Provides
    internal fun providePodcastQueries(
        @ApplicationContext context: Context,
        blacklistPrefs: BlacklistPreferences,
        sortPrefs: SortPreferences
    ): TrackQueries {
        return TrackQueries(
            context.contentResolver,
            blacklistPrefs,
            sortPrefs,
            true,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

}