package dev.olog.core.interactor.songlist

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import javax.inject.Inject


class GetSongListByParamUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastGateway: PodcastGateway,
    private val podcastArtistGateway: PodcastArtistGateway

) {

    operator fun invoke(mediaId: MediaId): List<Song> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getTrackListByParam(mediaId.categoryValue)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getTrackListByParam(mediaId.categoryId)
            MediaIdCategory.SONGS -> songGateway.getAll()
            MediaIdCategory.ALBUMS -> albumGateway.getTrackListByParam(mediaId.categoryId)
            MediaIdCategory.ARTISTS -> artistGateway.getTrackListByParam(mediaId.categoryId)
            MediaIdCategory.GENRES -> genreGateway.getTrackListByParam(mediaId.categoryId)
            MediaIdCategory.PODCASTS -> podcastGateway.getAll()
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.getTrackListByParam(mediaId.categoryId)
            MediaIdCategory.PODCASTS_AUTHOR -> podcastArtistGateway.getTrackListByParam(mediaId.categoryId)
            else -> throw AssertionError("invalid media id $mediaId")
        }
    }


}
