package dev.olog.domain.interactor

import dev.olog.domain.gateway.podcast.PodcastAlbumGateway
import dev.olog.domain.gateway.podcast.PodcastArtistGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.*
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetItemTitleUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,

    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastGateway: PodcastGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway
) {

    operator fun invoke(param: MediaId.Category): Flow<String> {
        return when (param.category){
            MediaIdCategory.FOLDERS -> folderGateway.observeByParam(param.categoryValue).map { it?.title }
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeByParam(param.categoryValue.toLong()).map { it?.title }
            MediaIdCategory.SONGS -> songGateway.observeByParam(param.categoryValue.toLong()).map { it?.title }
            MediaIdCategory.ALBUMS -> albumGateway.observeByParam(param.categoryValue.toLong()).map { it?.title }
            MediaIdCategory.ARTISTS -> artistGateway.observeByParam(param.categoryValue.toLong()).map { it?.name }
            MediaIdCategory.GENRES -> genreGateway.observeByParam(param.categoryValue.toLong()).map { it?.name }
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeByParam(param.categoryValue.toLong()).map { it?.title }
            MediaIdCategory.PODCASTS -> podcastGateway.observeByParam(param.categoryValue.toLong()).map { it?.title }
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.observeByParam(param.categoryValue.toLong()).map { it?.name }
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.observeByParam(param.categoryValue.toLong()).map { it?.title }
        }.map { it ?: "" }
    }

}