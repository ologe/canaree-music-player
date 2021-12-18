package dev.olog.data.index

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.AppInitializer
import dev.olog.core.schedulers.Schedulers
import dev.olog.shared.android.permission.Permission
import dev.olog.shared.android.permission.PermissionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import javax.inject.Inject
import javax.inject.Singleton

// TODO test
@Singleton
internal class IndexingRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val schedulers: Schedulers,
    private val appScope: CoroutineScope,
    private val permissionManager: PermissionManager,
    private val indexedPlayablesQueries: IndexedPlayablesQueries,
    private val indexingGenresQueries: IndexedGenresQueries,
    private val indexingPlaylistsQueries: IndexedPlaylistsQueries,
    private val mediaStoreManager: MediaStoreManager,
) : AppInitializer {

    private val contentResolver = context.contentResolver
    private val genrePlayablesObservers = mutableListOf<ContentObserver>()
    private val playlistPlayablesObservers = mutableListOf<ContentObserver>()

    override fun init() {
        appScope.launch(schedulers.io) {
            permissionManager.awaitPermission(Permission.Storage)

            launch { indexPlayables() }
            launch { indexGenres() }
            launch { indexPlaylists() }
        }
    }

    private suspend fun indexPlayables() {
        observeMediaStore(MediaStoreManager.playablesUri) {
            val playables = mediaStoreManager.playables()

            indexedPlayablesQueries.transaction {
                indexedPlayablesQueries.deleteAll()
                for (playable in playables) {
                    indexedPlayablesQueries.insert(playable)
                }
            }
        }
    }

    private suspend fun indexGenres() {
        observeMediaStore(MediaStoreManager.genresUri) {
            val genres = mediaStoreManager.genres()
            indexingGenresQueries.transaction {
                indexingGenresQueries.deleteAll()
                indexingGenresQueries.deleteAllPlayables()
                for (genre in genres) {
                    indexingGenresQueries.insert(genre)
                }
            }

            yield()
            genrePlayablesObservers.forEach { contentResolver.unregisterContentObserver(it) }
            genrePlayablesObservers.clear()

            for (genre in genres) {
                indexGenrePlayables(genre)
            }
        }
    }

    private suspend fun indexGenrePlayables(genre: Indexed_genres) {
        val itemsUri = MediaStoreManager.genrePlayablesUri(genre.id)
        val observer = observeMediaStore(itemsUri) {
            val items = mediaStoreManager.genreItems(genre.id)
            indexingGenresQueries.transaction {
                for (item in items) {
                    indexingGenresQueries.insertPlayable(item)
                }
            }
        }
        genrePlayablesObservers.add(observer)
    }

    private suspend fun indexPlaylists() {
        observeMediaStore(MediaStoreManager.playlistsUri) {
            val playlists = mediaStoreManager.playlists()
            indexingPlaylistsQueries.transaction {
                indexingPlaylistsQueries.deleteAll()
                indexingPlaylistsQueries.deleteAllPlayables()
                for (genre in playlists) {
                    indexingPlaylistsQueries.insert(genre)
                }
            }

            yield()
            playlistPlayablesObservers.forEach { contentResolver.unregisterContentObserver(it) }
            playlistPlayablesObservers.clear()

            for (playlist in playlists) {
                indexPlaylistPlayables(playlist)
            }
        }
    }

    private suspend fun indexPlaylistPlayables(playlist: Indexed_playlists) {
        val itemsUri = MediaStoreManager.playlistPlayablesUri(playlist.id)
        val observer = observeMediaStore(itemsUri) {
            val items = mediaStoreManager.playlistsItems(playlist.id)
            indexingPlaylistsQueries.transaction {
                for (item in items) {
                    indexingPlaylistsQueries.insertPlayable(item)
                }
            }
        }
        playlistPlayablesObservers.add(observer)
    }

    private suspend fun observeMediaStore(
        uri: Uri,
        action: suspend () -> Unit,
    ): ContentObserver {
        val contentUri = ContentUri(
            uri = uri,
            notifyForDescendants = true
        )

        val observer = DataObserver(appScope, schedulers.io) { action() }

        contentResolver.registerContentObserver(
            contentUri.uri,
            contentUri.notifyForDescendants,
            observer
        )

        withContext(schedulers.io) {
            action()
        }
        return observer
    }

}