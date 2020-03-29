package dev.olog.core.interactor.songlist

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ObserveSongListByParamUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val trackGateway: TrackGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastAuthorGateway: PodcastAuthorGateway

) {

    operator fun invoke(mediaId: MediaId): Flow<List<Song>> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.observeTrackListByParam(mediaId.categoryId)
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeTrackListByParam(mediaId.categoryId.toLong())
            MediaIdCategory.SONGS -> trackGateway.observeAllTracks()
            MediaIdCategory.ALBUMS -> albumGateway.observeTrackListByParam(mediaId.categoryId.toLong())
            MediaIdCategory.ARTISTS -> artistGateway.observeTrackListByParam(mediaId.categoryId.toLong())
            MediaIdCategory.GENRES -> genreGateway.observeTrackListByParam(mediaId.categoryId.toLong())
            MediaIdCategory.PODCASTS -> trackGateway.observeAllPodcasts()
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeTrackListByParam(mediaId.categoryId.toLong())
            MediaIdCategory.PODCASTS_AUTHORS -> podcastAuthorGateway.observeTrackListByParam(mediaId.categoryId.toLong())
        }
    }


}
