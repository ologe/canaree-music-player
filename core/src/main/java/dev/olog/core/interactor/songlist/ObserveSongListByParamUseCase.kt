package dev.olog.core.interactor.songlist

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.*
import dev.olog.core.interactor.base.FlowUseCaseWithParam
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ObserveSongListByParamUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val songDataStore: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val podcastGateway: PodcastGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway,
    private val autoPlaylistGateway: AutoPlaylistGateway,

) : FlowUseCaseWithParam<List<Song>, MediaId>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCase(mediaId: MediaId): Flow<List<Song>> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.observeTrackListById(mediaId.id)
            MediaIdCategory.PLAYLISTS -> playlistGateway.observeTrackListById(mediaId)
            MediaIdCategory.AUTO_PLAYLISTS -> autoPlaylistGateway.observeTrackListById(mediaId.id)
            MediaIdCategory.SONGS -> {
                if (mediaId.isPodcast) {
                    podcastGateway.observeAll()
                } else {
                    songDataStore.observeAll()
                }
            }
            MediaIdCategory.ALBUMS -> {
                if (mediaId.isPodcast) {
                    podcastAlbumGateway.observeTrackListByParam(mediaId.id)
                } else {
                    albumGateway.observeTrackListById(mediaId.id)
                }
            }
            MediaIdCategory.ARTISTS -> {
                if (mediaId.isPodcast) {
                    podcastArtistGateway.observeTrackListByParam(mediaId.id)
                } else {
                    artistGateway.observeTrackListById(mediaId.id)
                }
            }
            MediaIdCategory.GENRES -> genreGateway.observeTrackListById(mediaId.id)
            else -> throw AssertionError("invalid media id $mediaId")
        }
    }


}
