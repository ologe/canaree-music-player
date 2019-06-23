package dev.olog.data.repository

import android.content.Context
import android.provider.MediaStore
import dev.olog.core.PlaylistConstants
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.Id
import dev.olog.core.gateway.PlaylistGateway2
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.R
import dev.olog.data.mapper.toPlaylist
import dev.olog.data.queries.PlaylistQueries
import dev.olog.data.utils.queryAll
import dev.olog.data.utils.queryCountRow
import dev.olog.shared.assertBackground
import dev.olog.shared.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class PlaylistRepository2 @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences
) : BaseRepository<Playlist, Id>(context), PlaylistGateway2 {

    private val autoPlaylistTitles = context.resources.getStringArray(R.array.common_auto_playlists)
    private val queries = PlaylistQueries(contentResolver, blacklistPrefs, sortPrefs)

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, true)
    }

    override fun queryAll(): List<Playlist> {
        assertBackgroundThread()
        val cursor = queries.getAll()
        val playlists = contentResolver.queryAll(cursor) { it.toPlaylist() }
        return playlists.map { playlist ->
            // get the size for every playlist
            val sizeQueryCursor = queries.countPlaylistSize(playlist.id)
            val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
            playlist.copy(size = sizeQuery)
        }
    }

    override fun getByParam(param: Id): Playlist? {
        assertBackgroundThread()
        return channel.valueOrNull?.find { it.id == param }
    }

    override fun observeByParam(param: Id): Flow<Playlist?> {
        return channel.asFlow().map { it.find { it.id == param } }
            .assertBackground()
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        return listOf()
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        return flow { }
    }

    override fun getAllAutoPlaylists(): List<Playlist> {
        assertBackgroundThread()
        return listOf(
            createAutoPlaylist(PlaylistConstants.LAST_ADDED_ID, autoPlaylistTitles[0], 0),
            createAutoPlaylist(PlaylistConstants.FAVORITE_LIST_ID, autoPlaylistTitles[1], 0),
            createAutoPlaylist(PlaylistConstants.HISTORY_LIST_ID, autoPlaylistTitles[2], 0)
        )
    }

    private fun createAutoPlaylist(id: Long, title: String, listSize: Int) : Playlist {
        return Playlist(id, title, listSize)
    }
}