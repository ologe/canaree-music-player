package dev.olog.data.mediastore

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.AppInitializer
import dev.olog.core.ApplicationScope
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.mediastore.song.genre.MediaStoreGenreDao
import dev.olog.data.mediastore.song.playlist.MediaStorePlaylistDao
import dev.olog.platform.permission.Permission
import dev.olog.platform.permission.PermissionManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class IndexedMediaStoreInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val app: ApplicationScope,
    private val schedulers: Schedulers,
    private val mediaStoreAudioDao: MediaStoreAudioDao,
    private val mediaStoreGenreDao: MediaStoreGenreDao,
    private val mediaStorePlaylistDao: MediaStorePlaylistDao,
    private val mediaStoreQuery: MediaStoreQuery,
    private val permissionManager: PermissionManager,
) : AppInitializer {

    override fun init() {
        app.launch(schedulers.io) {
            permissionManager.awaitPermission(context, Permission.Storage)
            launch { populateAudio() }
            launch { populateGenres() }
            launch { populatePlaylists() }
        }
    }

    private suspend fun populateAudio() {
        mediaStoreAudioDao.replaceAll(mediaStoreQuery.queryAllAudio())
    }

    private suspend fun populateGenres() = coroutineScope {
        val genres = mediaStoreQuery.queryAllGenres()
        val songs = genres
            .map { async { mediaStoreQuery.queryAllGenreSongs(it.id) } }
            .awaitAll()
            .flatten()
        mediaStoreGenreDao.replaceAll(genres, songs)
    }

    private suspend fun populatePlaylists() = coroutineScope {
        val playlists = mediaStoreQuery.queryAllPlaylists()
        val songs = playlists
            .map { async { mediaStoreQuery.queryAllPlaylistSongs(it.id) } }
            .awaitAll()
            .flatten()
        mediaStorePlaylistDao.replaceAll(playlists, songs)
    }

}