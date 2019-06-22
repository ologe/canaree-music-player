package dev.olog.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore.Audio
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.Song
import dev.olog.core.gateway.Id
import dev.olog.core.gateway.SongGateway2
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.mapper.toSong
import dev.olog.data.queries.TrackQueries
import dev.olog.data.utils.queryAll
import dev.olog.data.utils.queryOne
import dev.olog.shared.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class SongRepository2 @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences
) : BaseRepository<Song, Id>(context), SongGateway2 {

    private val queries = TrackQueries(
        context.contentResolver, blacklistPrefs,
        sortPrefs, false
    )

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    override fun queryAll(): List<Song> {
        assertBackgroundThread()
        val cursor = queries.getAll()
        return context.contentResolver.queryAll(cursor) { it.toSong() }
    }

    override fun getByParam(param: Id): Song? {
        assertBackgroundThread()
        val cursor = queries.getByParam(param)
        return context.contentResolver.queryOne(cursor) { it.toSong() }
    }

    override fun observeByParam(param: Id): Flow<Song?> {
        assertBackgroundThread()

        val uri = ContentUris.withAppendedId(Audio.Media.EXTERNAL_CONTENT_URI, param)
        val contentUri = ContentUri(uri, true)
        return observeByParamInternal(contentUri) { getByParam(param) }
    }

}