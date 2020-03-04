package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetItemTitleUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,

    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway

) {


    operator fun invoke(mediaId: MediaId): Flow<String?> {
        return when (mediaId.category){
            MediaIdCategory.FOLDERS -> folderGateway.observeByParam(mediaId.categoryValue).map { it?.title }
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeByParam(mediaId.categoryId).map { it?.title }
            MediaIdCategory.ALBUMS -> albumGateway.observeByParam(mediaId.categoryId).map { it?.title }
            MediaIdCategory.ARTISTS -> artistGateway.observeByParam(mediaId.categoryId).map { it?.name }
            MediaIdCategory.GENRES -> genreGateway.observeByParam(mediaId.categoryId).map { it?.name }
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeByParam(mediaId.categoryId).map { it?.title }
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.observeByParam(mediaId.categoryId).map { it?.name }
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.observeByParam(mediaId.categoryId).map { it?.title }
            else -> throw IllegalArgumentException("invalid media category ${mediaId.category}")
        }
    }

}