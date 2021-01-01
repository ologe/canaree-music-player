package dev.olog.domain.interactor.songlist

import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.podcast.PodcastAlbumGateway
import dev.olog.domain.gateway.podcast.PodcastArtistGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.*
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import javax.inject.Inject

@Deprecated("delete, use observe.first() instead")
class GetSongListByParamUseCase @Inject constructor(
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

    suspend operator fun invoke(mediaId: MediaId): List<Track> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getTrackListByParam(mediaId.categoryValue)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.SONGS -> songGateway.getAll()
            MediaIdCategory.ALBUMS -> albumGateway.getTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.ARTISTS -> artistGateway.getTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.GENRES -> genreGateway.getTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS -> podcastGateway.getAll()
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.getTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.getTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.getTrackListByParam(mediaId.categoryValue.toLong())
        }
    }


}
