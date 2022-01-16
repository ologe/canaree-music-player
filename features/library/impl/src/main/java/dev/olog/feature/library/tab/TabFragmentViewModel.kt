package dev.olog.feature.library.tab

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.core.author.AuthorGateway
import dev.olog.core.collection.CollectionGateway
import dev.olog.core.folder.FolderGateway
import dev.olog.core.genre.GenreGateway
import dev.olog.core.playlist.PlaylistGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.core.sort.AuthorSort
import dev.olog.core.sort.CollectionSort
import dev.olog.core.sort.GenericSort
import dev.olog.core.sort.TrackSort
import dev.olog.core.track.TrackGateway
import dev.olog.feature.base.adapter.media.MediaListItem
import dev.olog.feature.library.LibraryPrefs
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TabFragmentViewModel @Inject constructor(
    private val dataProvider: TabFragmentDataProvider,
    private val playableGateway: TrackGateway,
    private val authorGateway: AuthorGateway,
    private val collectionGateway: CollectionGateway,
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway,
    private val libraryPrefs: LibraryPrefs,
    savedStateHandle: SavedStateHandle,
    schedulers: Schedulers,
) : ViewModel() {

    private val category = savedStateHandle.get<MediaUri.Category>(TabFragment.CATEGORY)!!
    private val type = savedStateHandle.get<MediaStoreType>(TabFragment.TYPE)!!

    val data: Flow<List<MediaListItem>>
        get() = dataProvider.get(category, type)

    val recentlyAdded: Flow<List<MediaListItem>>
        get() = dataProvider.recentlyAdded(category, type)

    val recentlyPlayed: Flow<List<MediaListItem>>
        get() = dataProvider.recentlyPlayed(category, type)

    val autoPlaylists: Flow<List<MediaListItem>>
        get() = dataProvider.autoPlaylists(category, type)

    fun spanCount() = libraryPrefs.spanCount(category, type)

    fun getCurrentSorting(item: MediaListItem): String {
        return when (category) {
            MediaUri.Category.Folder -> getSortingForFolder(item as MediaListItem.Collection)
            MediaUri.Category.Playlist -> getSortingForPlaylist(item as MediaListItem.Collection)
            MediaUri.Category.Track -> getSortingForTracks(item as MediaListItem.Track)
            MediaUri.Category.Author -> getSortingForAuthor(item as MediaListItem.Author)
            MediaUri.Category.Collection -> getSortingForCollection(item as MediaListItem.Collection)
            MediaUri.Category.Genre -> getSortingForGenre(item as MediaListItem.Collection)
        }
    }

    private fun getSortingForTracks(item: MediaListItem.Track): String {
        val sort = playableGateway.getSort(type)
        return when (sort.type) {
            TrackSort.Title -> item.title
            TrackSort.Author -> item.author
            TrackSort.Collection -> item.collection
            TrackSort.Duration,
            TrackSort.DateAdded -> error("invalid ${sort.type} for item ${item.uri}")
        }
    }

    private fun getSortingForAuthor(item: MediaListItem.Author): String {
        val sort = authorGateway.getSort(type)
        return when (sort.type) {
            AuthorSort.Name -> item.title
        }
    }

    private fun getSortingForCollection(item: MediaListItem.Collection): String {
        val sort = collectionGateway.getSort(type)
        return when (sort.type) {
            CollectionSort.Title -> item.title
            CollectionSort.Author -> item.subtitle
        }
    }

    private fun getSortingForFolder(item: MediaListItem.Collection): String {
        val sort = folderGateway.getSort()
        return when (sort.type) {
            GenericSort.Title -> item.title
        }
    }

    private fun getSortingForGenre(item: MediaListItem.Collection): String {
        val sort = genreGateway.getSort()
        return when (sort.type) {
            GenericSort.Title -> item.title
        }
    }

    private fun getSortingForPlaylist(item: MediaListItem.Collection): String {
        val sort = playlistGateway.getSort(type)
        return when (sort.type) {
            GenericSort.Title -> item.title
        }
    }

}