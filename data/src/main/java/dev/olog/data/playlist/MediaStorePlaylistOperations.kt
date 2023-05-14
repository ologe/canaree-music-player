package dev.olog.data.playlist

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore.Audio.Playlists
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.mediastore.MediaStorePlaylistInternalEntity
import dev.olog.data.mediastore.MediaStoreQuery
import dev.olog.data.mediastore.MediaStoreUris
import dev.olog.data.utils.ContentValues
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("DEPRECATION")
class MediaStorePlaylistOperations @Inject constructor(
    @ApplicationContext private val context: Context,
    private val schedulers: Schedulers,
    private val queries: MediaStoreQuery,
) : PlaylistOperations {

    private val contentResolver: ContentResolver
        get() = context.contentResolver

    override suspend fun createPlaylist(
        title: String
    ): MediaStorePlaylistInternalEntity? = withContext(schedulers.io) {
        val uri = contentResolver.insert(
            MediaStoreUris.playlists,
            ContentValues(Playlists.NAME to title)
        )
        if (uri != null) {
            return@withContext queries.queryLastAddedPlaylistByTitle(title)
        }
        Log.e("MediaStorePlaylist", "playlist $title not created")
        return@withContext null
    }

    override suspend fun renamePlaylist(
        playlist: Playlist,
        title: String
    ): Boolean = withContext(schedulers.io) {
        val rowsUpdated = contentResolver.update(
            MediaStoreUris.playlists,
            ContentValues(Playlists.NAME to title),
            "${Playlists._ID} = ?",
            arrayOf("${playlist.id}")
        )
        if (rowsUpdated < 1) {
            Log.e("MediaStorePlaylist", "playlist $title not renamed")
        }
        return@withContext rowsUpdated > 0
    }

    override suspend fun deletePlaylist(
        playlist: Playlist,
    ): Boolean = withContext(schedulers.io) {
        val rowsDeleted = contentResolver.delete(
            MediaStoreUris.playlists,
            "${Playlists._ID} = ?",
            arrayOf("${playlist.id}")
        )
        if (rowsDeleted < 1) {
            Log.e("MediaStorePlaylist", "playlist ${playlist.id} not deleted")
        }
        return@withContext rowsDeleted > 0
    }

    override suspend fun clearPlaylist(
        playlist: Playlist,
    ): Boolean = withContext(schedulers.io) {
        val rowsDeleted = contentResolver.delete(
            MediaStoreUris.playlistTracks(playlist.id),
            null,
            null,
        )
        if (rowsDeleted < 1) {
            Log.e("MediaStorePlaylist", "playlist ${playlist.id} not cleared")
        }
        return@withContext rowsDeleted > 0
    }

    override suspend fun addSongsToPlaylist(
        playlist: Playlist,
        songs: List<Song>
    ): List<MediaStoreQuery.PlaylistTrack> = withContext(schedulers.io) {
        val maxPlayOrder = queries.queryPlaylistsTracks(playlist.id).maxOfOrNull { it.playOrder } ?: 0
        val values = songs.mapIndexed { index, song ->
            ContentValues(
                Playlists.Members.PLAY_ORDER to maxPlayOrder + 1 + index,
                Playlists.Members.AUDIO_ID to song.id,
                Playlists.Members.PLAYLIST_ID to playlist.id,
            )
        }

        val rowsInserted = contentResolver.bulkInsert(
            MediaStoreUris.playlistTracks(playlist.id),
            values.toTypedArray(),
        )
        if (rowsInserted > 0) {
            return@withContext queries.queryPlaylistsTracks(playlist.id)
        }
        Log.e("MediaStorePlaylist", "songs not added to playlist ${playlist.id}")
        return@withContext emptyList()
    }

    override suspend fun removeFromPlaylist(
        playlist: Playlist,
        idInPlaylist: Long
    ): Boolean = withContext(schedulers.io) {
        val rowsDeleted = contentResolver.delete(
            MediaStoreUris.playlistTracks(playlist.id),
            "${Playlists.Members._ID} = ?",
            arrayOf("$idInPlaylist")
        )
        if (rowsDeleted < 1) {
            Log.e("MediaStorePlaylist", "songs $idInPlaylist not removed from playlist ${playlist.id}")
        }
        return@withContext rowsDeleted > 0
    }

    override suspend fun overridePlaylistMembers(
        playlist: Playlist,
        songs: List<Song>,
    ): List<MediaStoreQuery.PlaylistTrack> = withContext(schedulers.io) {
        clearPlaylist(playlist)
        addSongsToPlaylist(playlist, songs)
        return@withContext queries.queryPlaylistsTracks(playlist.id)
    }

    // TODO check works correctly
    override suspend fun moveItem(
        playlist: Playlist,
        moveList: List<Pair<Int, Int>>
    ): Int = withContext(schedulers.io) {
        val rowsUpdated = moveList.count { (from, to) ->
            Playlists.Members.moveItem(contentResolver, playlist.id, from, to)
        }
        if (rowsUpdated < 1) {
            Log.e("MediaStorePlaylist", "no items moved in playlist ${playlist.id}")
        }
        return@withContext rowsUpdated
    }
}