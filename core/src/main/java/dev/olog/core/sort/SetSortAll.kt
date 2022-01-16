package dev.olog.core.sort

import dev.olog.core.MediaStoreType
import dev.olog.core.author.AuthorGateway
import dev.olog.core.collection.CollectionGateway
import dev.olog.core.folder.FolderGateway
import dev.olog.core.genre.GenreGateway
import dev.olog.core.track.TrackGateway
import dev.olog.core.playlist.PlaylistGateway
import dev.olog.core.sort.*
import javax.inject.Inject

class SetSortAll @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val authorGateway: AuthorGateway,
    private val collectionGateway: CollectionGateway,
    private val genreGateway: GenreGateway,
    private val trackGateway: TrackGateway,
) {

    fun setFolder(sort: Sort<GenericSort>) = folderGateway.setSort(sort)
    fun setPlaylist(type: MediaStoreType, sort: Sort<GenericSort>) = playlistGateway.setSort(type, sort)
    fun setTrack(type: MediaStoreType, sort: Sort<TrackSort>) = trackGateway.setSort(type, sort)
    fun setCollection(type: MediaStoreType, sort: Sort<CollectionSort>) = collectionGateway.setSort(type, sort)
    fun setAuthor(type: MediaStoreType, sort: Sort<AuthorSort>) = authorGateway.setSort(type, sort)
    fun setGenre(sort: Sort<GenericSort>) = genreGateway.setSort(sort)

}