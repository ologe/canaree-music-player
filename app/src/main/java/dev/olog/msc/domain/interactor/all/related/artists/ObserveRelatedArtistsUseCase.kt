package dev.olog.msc.domain.interactor.all.related.artists

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Artist
import dev.olog.core.gateway.FolderGateway
import dev.olog.core.gateway.GenreGateway
import dev.olog.core.gateway.PlaylistGateway2
import dev.olog.core.gateway.PodcastPlaylistGateway
import dev.olog.msc.domain.interactor.base.FlowUseCaseWithParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObserveRelatedArtistsUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway2,
    private val genreGateway: GenreGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) : FlowUseCaseWithParam<List<Artist>, MediaId>() {

    override fun buildUseCase(mediaId: MediaId): Flow<List<Artist>> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.observeRelatedArtists(mediaId.categoryValue)
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeRelatedArtists(mediaId.categoryId)
            MediaIdCategory.GENRES -> genreGateway.observeRelatedArtists(mediaId.categoryId)
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeRelatedArtists(mediaId.categoryId)
            else -> flow {  }
        }
    }
}