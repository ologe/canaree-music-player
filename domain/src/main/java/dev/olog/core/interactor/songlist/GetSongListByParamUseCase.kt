package dev.olog.core.interactor.songlist

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.shared.throwNotHandled
import javax.inject.Inject


class GetSongListByParamUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val trackGateway: TrackGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastAuthorGateway: PodcastAuthorGateway

) {

    operator fun invoke(mediaId: MediaId): List<Song> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getTrackListByParam(mediaId.categoryId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getTrackListByParam(mediaId.categoryId.toLong())
            MediaIdCategory.SONGS -> trackGateway.getAllTracks()
            MediaIdCategory.ALBUMS -> albumGateway.getTrackListByParam(mediaId.categoryId.toLong())
            MediaIdCategory.ARTISTS -> artistGateway.getTrackListByParam(mediaId.categoryId.toLong())
            MediaIdCategory.GENRES -> genreGateway.getTrackListByParam(mediaId.categoryId.toLong())
            MediaIdCategory.PODCASTS -> trackGateway.getAllPodcasts()
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.getTrackListByParam(mediaId.categoryId.toLong())
            MediaIdCategory.PODCASTS_AUTHORS -> podcastAuthorGateway.getTrackListByParam(mediaId.categoryId.toLong())
            MediaIdCategory.SPOTIFY_ALBUMS -> throwNotHandled(mediaId)
            MediaIdCategory.SPOTIFY_TRACK -> throwNotHandled(mediaId)
        }
    }


}
