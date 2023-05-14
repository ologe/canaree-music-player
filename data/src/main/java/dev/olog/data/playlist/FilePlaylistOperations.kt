package dev.olog.data.playlist

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.mediastore.MediaStorePlaylistInternalEntity
import dev.olog.data.mediastore.MediaStoreQuery
import dev.olog.data.mediastore.MediaStoreScanner
import dev.olog.data.mediastore.playlist.MediaStorePlaylistDirectoryRepository
import dev.olog.data.playlist.persister.PersistablePlaylist
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.Q)
class FilePlaylistOperations @Inject constructor(
    @ApplicationContext private val context: Context,
    private val schedulers: Schedulers,
    private val playlistDirectoryRepository: MediaStorePlaylistDirectoryRepository,
    private val queries: MediaStoreQuery,
    private val scanner: MediaStoreScanner,
) : PlaylistOperations {

    companion object {
        private const val M3U_MIMETYPE = "application/xspf+xml"
    }

    override suspend fun createPlaylist(
        title: String
    ): MediaStorePlaylistInternalEntity? = withContext(schedulers.io) {
        val document = documentTree()?.createFile(M3U_MIMETYPE, title) ?: return@withContext null
        document.editPlaylist {
            clear()
        }
        return@withContext queries.queryLastAddedPlaylistByTitle(title)
    }

    override suspend fun renamePlaylist(
        playlist: Playlist,
        title: String
    ): Boolean = withContext(schedulers.io) {
        val extension = File(playlist.path.orEmpty()).extension
        val document = findPlaylistFile(playlist)
        val renamed = document?.renameTo("$title.${extension}")
        rescanPlaylist(playlist)
        renamed == true
    }

    override suspend fun deletePlaylist(
        playlist: Playlist,
    ): Boolean = withContext(schedulers.io) {
        val document = findPlaylistFile(playlist)
        val deleted = document?.delete()
        rescanPlaylist(playlist)
        deleted == true
    }

    override suspend fun clearPlaylist(
        playlist: Playlist,
    ): Boolean = withContext(schedulers.io) {
        val document = findPlaylistFile(playlist) ?: return@withContext false
        document.editPlaylist {
            clear()
        }
        rescanPlaylist(playlist)
        return@withContext true
    }

    override suspend fun addSongsToPlaylist(
        playlist: Playlist,
        songs: List<Song>
    ): List<MediaStoreQuery.PlaylistTrack> = withContext(schedulers.io) {
        val document = findPlaylistFile(playlist) ?: return@withContext emptyList()
        document.editPlaylist {
            for (song in songs) {
                add(song.path)
            }
        }
        rescanPlaylist(playlist)
        return@withContext queries.queryPlaylistsTracks(playlist.id)
    }

    override suspend fun removeFromPlaylist(
        playlist: Playlist,
        idInPlaylist: Long
    ): Boolean = withContext(schedulers.io) {
        val document = findPlaylistFile(playlist) ?: return@withContext false
        document.editPlaylist {
            remove(idInPlaylist.toInt())
        }
        rescanPlaylist(playlist)
        return@withContext true
    }

    override suspend fun overridePlaylistMembers(
        playlist: Playlist,
        songs: List<Song>,
    ): List<MediaStoreQuery.PlaylistTrack> = withContext(schedulers.io) {
        val document = findPlaylistFile(playlist) ?: return@withContext emptyList()
        document.editPlaylist {
            clear()
            addAll(songs.map { it.path })
        }
        rescanPlaylist(playlist)
        return@withContext queries.queryPlaylistsTracks(playlist.id)
    }

    override suspend fun moveItem(
        playlist: Playlist,
        moveList: List<Pair<Int, Int>>
    ): Int = withContext(schedulers.io) {
        val document = findPlaylistFile(playlist) ?: return@withContext -1
        document.editPlaylist {
            for ((from, to) in moveList) {
                move(from, to)
            }
        }
        rescanPlaylist(playlist)
        return@withContext moveList.size
    }

    private fun documentTree(): DocumentFile? {
        val documentUri = playlistDirectoryRepository.get()
        return documentUri?.let { DocumentFile.fromTreeUri(context, it) }
    }

    private fun findPlaylistFile(playlist: Playlist): DocumentFile? {
        val path = playlist.path ?: return null
        val displayName = File(path).name
        return documentTree()?.findFile(displayName)
    }

    private fun DocumentFile.editPlaylist(editor: PersistablePlaylist.() -> Unit) {
        val playlist = PersistablePlaylist()
        playlist.read(this, context.contentResolver)
        editor(playlist)
        playlist.write(this, context.contentResolver)
    }

    private suspend fun rescanPlaylist(playlist: Playlist) {
        playlist.path?.let { scanner.scanFilePath(it) }
    }

}