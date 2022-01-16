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

class GetSortAll @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val authorGateway: AuthorGateway,
    private val collectionGateway: CollectionGateway,
    private val genreGateway: GenreGateway,
    private val trackGateway: TrackGateway,
) {

    fun getFolder(sort: Sort<GenericSort>) = folderGateway.getSort()
    fun getPlaylist(type: MediaStoreType, sort: Sort<GenericSort>) = playlistGateway.getSort(type)
    fun getTrack(type: MediaStoreType, sort: Sort<TrackSort>) = trackGateway.getSort(type)
    fun getCollection(type: MediaStoreType, sort: Sort<CollectionSort>) = collectionGateway.getSort(type)
    fun getAuthor(type: MediaStoreType, sort: Sort<AuthorSort>) = authorGateway.getSort(type)
    fun getGenre(sort: Sort<GenericSort>) = genreGateway.getSort()

}