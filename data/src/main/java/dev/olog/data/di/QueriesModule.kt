package dev.olog.data.di

import android.content.ContentResolver
import android.provider.MediaStore
import dagger.Module
import dagger.Provides
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.domain.prefs.SortPreferences
import dev.olog.data.di.qualifier.Podcast
import dev.olog.data.di.qualifier.Tracks
import dev.olog.data.queries.TrackQueries

@Module
class QueriesModule {

    @Tracks
    @Provides
    internal fun provideTracksQueries(
        contentResolver: ContentResolver,
        blacklistPrefs: BlacklistPreferences,
        sortPrefs: SortPreferences
    ): TrackQueries {
        return TrackQueries(
            contentResolver,
            blacklistPrefs,
            sortPrefs,
            false,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    @Podcast
    @Provides
    internal fun providePodcastQueries(
        contentResolver: ContentResolver,
        blacklistPrefs: BlacklistPreferences,
        sortPrefs: SortPreferences
    ): TrackQueries {
        return TrackQueries(
            contentResolver,
            blacklistPrefs,
            sortPrefs,
            true,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

}