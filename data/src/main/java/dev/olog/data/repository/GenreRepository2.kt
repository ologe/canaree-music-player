package dev.olog.data.repository

import android.content.Context
import android.provider.MediaStore
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.GenreGateway2
import dev.olog.core.gateway.Id
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.mapper.toGenre
import dev.olog.data.queries.GenreQueries
import dev.olog.data.utils.queryAll
import dev.olog.data.utils.queryCountRow
import dev.olog.shared.assertBackground
import dev.olog.shared.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GenreRepository2 @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences
) : BaseRepository<Genre, Id>(context), GenreGateway2 {

    private val queries = GenreQueries(contentResolver, blacklistPrefs, sortPrefs)

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, true)
    }

    override fun queryAll(): List<Genre> {
        assertBackgroundThread()
        val cursor = queries.getAll()
        val genres = contentResolver.queryAll(cursor) { it.toGenre() }
        return genres.map { genre ->
            // get the size for every playlist
            val sizeQueryCursor = queries.countGenreSize(genre.id)
            val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
            genre.copy(size = sizeQuery)
        }
    }

    override fun getByParam(param: Id): Genre? {
        assertBackgroundThread()
        return channel.valueOrNull?.find { it.id == param }
    }

    override fun observeByParam(param: Id): Flow<Genre?> {
        return channel.asFlow().map { it.find { it.id == param } }
            .assertBackground()
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        return listOf()
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        return flow { }
    }
}