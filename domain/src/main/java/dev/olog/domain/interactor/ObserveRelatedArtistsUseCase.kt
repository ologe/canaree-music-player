package dev.olog.domain.interactor

import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.FolderGateway
import dev.olog.domain.gateway.track.GenreGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveRelatedArtistsUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) {

    operator fun invoke(mediaId: MediaId): Flow<List<Artist>> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.observeRelatedArtists(mediaId.categoryId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeRelatedArtists(mediaId.categoryId.toLong())
            MediaIdCategory.GENRES -> genreGateway.observeRelatedArtists(mediaId.categoryId.toLong())
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeRelatedArtists(mediaId.categoryId.toLong())
            // TODO GENERATED_PLAYLISTS
            else -> flowOf(listOf())
        }
    }
}