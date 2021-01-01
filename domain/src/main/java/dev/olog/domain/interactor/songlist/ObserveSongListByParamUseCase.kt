package dev.olog.domain.interactor.songlist

import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.podcast.PodcastAlbumGateway
import dev.olog.domain.gateway.podcast.PodcastArtistGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.*
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Deprecated("rename to ObserveSongListUseCase")
class ObserveSongListByParamUseCase @Inject constructor(
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

    operator fun invoke(mediaId: MediaId): Flow<List<Track>> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.observeTrackListByParam(mediaId.categoryValue)
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.SONGS -> songDataStore.observeAll()
            MediaIdCategory.ALBUMS -> albumGateway.observeTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.ARTISTS -> artistGateway.observeTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.GENRES -> genreGateway.observeTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS -> podcastGateway.observeAll()
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.observeTrackListByParam(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.observeTrackListByParam(mediaId.categoryValue.toLong())
        }
    }


}
