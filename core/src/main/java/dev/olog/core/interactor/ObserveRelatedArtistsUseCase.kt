package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Artist
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.interactor.base.FlowUseCaseWithParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveRelatedArtistsUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) : FlowUseCaseWithParam<List<Artist>, MediaId>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCase(mediaId: MediaId): Flow<List<Artist>> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.observeRelatedArtists(mediaId.categoryId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeRelatedArtists(mediaId.categoryId)
            MediaIdCategory.GENRES -> genreGateway.observeRelatedArtists(mediaId.categoryId)
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeRelatedArtists(mediaId.categoryId)
            else -> flowOf(listOf())
        }
    }
}