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
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTracksByCategoryUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val authorGateway: AuthorGateway,
    private val collectionGateway: CollectionGateway,
    private val genreGateway: GenreGateway,
    private val trackGateway: TrackGateway,
) {

    operator fun invoke(uri: MediaUri): Flow<List<Song>> {
        return when (uri.category) {
            MediaUri.Category.Folder -> folderGateway.observeTracksById(uri)
            MediaUri.Category.Playlist -> playlistGateway.observeTracksById(uri)
            MediaUri.Category.Author -> authorGateway.observeTracksById(uri)
            MediaUri.Category.Collection -> collectionGateway.observeTracksById(uri)
            MediaUri.Category.Genre -> genreGateway.observeTracksById(uri)
            MediaUri.Category.Track -> {
                val type = when (uri.isPodcast) {
                    true -> MediaStoreType.Podcast
                    false -> MediaStoreType.Song
                }
                trackGateway.observeAll(type)
            }
        }
    }

}
