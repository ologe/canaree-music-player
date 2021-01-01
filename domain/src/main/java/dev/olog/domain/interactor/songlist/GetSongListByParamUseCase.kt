package dev.olog.domain.interactor.songlist

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.podcast.PodcastAlbumGateway
import dev.olog.domain.gateway.podcast.PodcastArtistGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.*
import javax.inject.Inject


class GetSongListByParamUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val songDataStore: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastGateway: PodcastGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway

) {

    suspend operator fun invoke(mediaId: MediaId): List<Track> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getTrackListByParam(mediaId.categoryValue)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getTrackListByParam(mediaId.categoryId)
            MediaIdCategory.SONGS -> songDataStore.getAll()
            MediaIdCategory.ALBUMS -> albumGateway.getTrackListByParam(mediaId.categoryId)
            MediaIdCategory.ARTISTS -> artistGateway.getTrackListByParam(mediaId.categoryId)
            MediaIdCategory.GENRES -> genreGateway.getTrackListByParam(mediaId.categoryId)
            MediaIdCategory.PODCASTS -> podcastGateway.getAll()
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.getTrackListByParam(mediaId.categoryId)
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.getTrackListByParam(mediaId.categoryId)
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.getTrackListByParam(mediaId.categoryId)
        }
    }


}
