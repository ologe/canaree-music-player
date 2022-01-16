package dev.olog.core.interactor

import dev.olog.core.MediaStoreType
import dev.olog.core.author.AuthorGateway
import dev.olog.core.collection.CollectionGateway
import dev.olog.core.folder.FolderGateway
import dev.olog.core.genre.GenreGateway
import dev.olog.core.MediaUri
import dev.olog.core.track.Song
import dev.olog.core.playlist.PlaylistGateway
import dev.olog.core.track.TrackGateway
import javax.inject.Inject

class GetTracksByCategoryUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val authorGateway: AuthorGateway,
    private val collectionGateway: CollectionGateway,
    private val genreGateway: GenreGateway,
    private val trackGateway: TrackGateway,
) {

    operator fun invoke(uri: MediaUri): List<Song> {
        return when (uri.category) {
            MediaUri.Category.Folder -> folderGateway.getTracksById(uri)
            MediaUri.Category.Playlist -> playlistGateway.getTracksById(uri)
            MediaUri.Category.Author -> authorGateway.getTracksById(uri)
            MediaUri.Category.Collection -> collectionGateway.getTracksById(uri)
            MediaUri.Category.Genre -> genreGateway.getTracksById(uri)
            MediaUri.Category.Track -> {
                val type = when (uri.isPodcast) {
                    true -> MediaStoreType.Podcast
                    false -> MediaStoreType.Song
                }
                trackGateway.getAll(type)
            }
        }
    }

}