package dev.olog.feature.library.tab

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.core.author.AuthorGateway
import dev.olog.core.collection.CollectionGateway
import dev.olog.core.folder.FolderGateway
import dev.olog.core.genre.GenreGateway
import dev.olog.core.playlist.PlaylistGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.core.track.TrackGateway
import dev.olog.feature.base.adapter.media.MediaListItem
import dev.olog.feature.library.LibraryPrefs
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TabFragmentDataProvider @Inject constructor(
    @ApplicationContext context: Context,
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val trackGateway: TrackGateway,
    private val collectionGateway: CollectionGateway,
    private val authorGateway: AuthorGateway,
    private val genreGateway: GenreGateway,
    private val libraryPrefs: LibraryPrefs,
    private val schedulers: Schedulers,
) {

    private val resources = context.resources

    fun autoPlaylists(category: MediaUri.Category, type: MediaStoreType): Flow<List<MediaListItem>> {
        if (category != MediaUri.Category.Playlist) {
            return emptyFlow()
        }
        return playlistGateway.observeAutoPlaylist(type)
            .mapListItem { it.toMediaListItem(resources) }
    }

    fun recentlyPlayed(category: MediaUri.Category, type: MediaStoreType): Flow<List<MediaListItem>> {
        return when (category) {
            MediaUri.Category.Folder,
            MediaUri.Category.Playlist,
            MediaUri.Category.Track,
            MediaUri.Category.Genre -> emptyFlow()
            MediaUri.Category.Author -> recentlyPlayedAuthors(type)
            MediaUri.Category.Collection -> recentlyPlayedCollections(type)
        }
    }

    fun recentlyAdded(category: MediaUri.Category, type: MediaStoreType): Flow<List<MediaListItem>> {
        return when (category) {
            MediaUri.Category.Folder,
            MediaUri.Category.Playlist,
            MediaUri.Category.Track,
            MediaUri.Category.Genre -> emptyFlow()
            MediaUri.Category.Author -> recentlyAddedAuthors(type)
            MediaUri.Category.Collection -> recentlyAddedCollections(type)
        }
    }

    private fun recentlyPlayedCollections(type: MediaStoreType): Flow<List<MediaListItem>> {
        return combine(
            collectionGateway.observeRecentlyPlayed(type),
            libraryPrefs.recentlyPlayedVisibility.observe()
        ) { items, canShow -> if (canShow) items else emptyList() }
            .mapListItem { it.toMediaListItem() }
    }

    private fun recentlyAddedCollections(type: MediaStoreType): Flow<List<MediaListItem>> {
        return combine(
            collectionGateway.observeRecentlyAdded(type),
            libraryPrefs.recentlyAddedVisibility.observe()
        ) { items, canShow -> if (canShow) items else emptyList() }
            .mapListItem { it.toMediaListItem() }
    }

    private fun recentlyPlayedAuthors(type: MediaStoreType): Flow<List<MediaListItem>> {
        return combine(
            authorGateway.observeRecentlyPlayed(type),
            libraryPrefs.recentlyPlayedVisibility.observe()
        ) { items, canShow -> if (canShow) items else emptyList() }
            .mapListItem { it.toMediaListItem(resources) }
    }

    private fun recentlyAddedAuthors(type: MediaStoreType): Flow<List<MediaListItem>> {
        return combine(
            authorGateway.observeRecentlyAdded(type),
            libraryPrefs.recentlyAddedVisibility.observe()
        ) { items, canShow -> if (canShow) items else emptyList() }
            .mapListItem { it.toMediaListItem(resources) }
    }

    fun get(
        category: MediaUri.Category,
        type: MediaStoreType,
    ): Flow<List<MediaListItem>> = when (category) {
        MediaUri.Category.Folder -> getFolders()
        MediaUri.Category.Playlist -> getPlaylist(type)
        MediaUri.Category.Track -> getTracks(type)
        MediaUri.Category.Collection -> getAlbums(type)
        MediaUri.Category.Author -> getArtists(type)
        MediaUri.Category.Genre -> getGenres()
    }.flowOn(schedulers.cpu)

    private fun getFolders(): Flow<List<MediaListItem>> {
        return folderGateway.observeAll()
            .mapListItem { it.toMediaListItem(resources) }
    }

    private fun getGenres(): Flow<List<MediaListItem>> {
        return genreGateway.observeAll()
            .mapListItem { it.toMediaListItem(resources) }
    }

    private fun getPlaylist(type: MediaStoreType): Flow<List<MediaListItem>> {
        return playlistGateway.observeAll(type)
            .mapListItem { it.toMediaListItem(resources) }
    }

    private fun getAlbums(type: MediaStoreType): Flow<List<MediaListItem>> {
        return collectionGateway.observeAll(type)
            .mapListItem { it.toMediaListItem() }
    }

    private fun getArtists(type: MediaStoreType): Flow<List<MediaListItem>> {
        return authorGateway.observeAll(type)
            .mapListItem { it.toMediaListItem(resources) }
    }

    private fun getTracks(type: MediaStoreType): Flow<List<MediaListItem>> {
        return trackGateway.observeAll(type)
            .mapListItem { it.toMediaListItem() }
    }

}