package dev.olog.data.repository

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.Album
import dev.olog.core.entity.Song
import dev.olog.core.gateway.AlbumGateway2
import dev.olog.core.gateway.Id
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.mapper.toAlbum
import dev.olog.data.queries.AlbumsQueries
import dev.olog.data.utils.queryAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AlbumRepository2 @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences
) : BaseRepository<Album, Id>(context), AlbumGateway2 {

    private val queries = AlbumsQueries(context.contentResolver, blacklistPrefs, sortPrefs, false)

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    private fun extractAlbums(cursor: Cursor): List<Album> {
        return context.contentResolver.queryAll(cursor) { it.toAlbum() }
            .groupBy { it.id }
            .map { (_, list) ->
                val album = list[0]
                album.copy(songs = list.size)
            }
    }

    override fun queryAll(): List<Album> {
        val cursor = queries.getAll()
        return extractAlbums(cursor)
    }

    override fun getByParam(param: Id): Album? {
        return channel.valueOrNull?.find { it.id == param }
    }

    override fun observeByParam(param: Id): Flow<Album?> {
        return channel.asFlow().map { list -> list.find { it.id == param } }
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        return listOf()
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        return flow {  }
    }
}