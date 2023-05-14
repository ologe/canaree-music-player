package dev.olog.core.interactor.songlist

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.*
import javax.inject.Inject


class GetSongListByParamUseCase @Inject constructor(
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
) {

    operator fun invoke(mediaId: MediaId): List<Song> {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.getTrackListById(mediaId.id)
            MediaIdCategory.PLAYLISTS -> playlistGateway.getTrackListById(mediaId)
            MediaIdCategory.AUTO_PLAYLISTS -> autoPlaylistGateway.getTrackListById(mediaId.id)
            MediaIdCategory.SONGS -> {
                if (mediaId.isPodcast) {
                    podcastGateway.getAll()
                } else {
                    songDataStore.getAll()
                }
            }
            MediaIdCategory.ALBUMS -> {
                if (mediaId.isPodcast) {
                    podcastAlbumGateway.getTrackListByParam(mediaId.id)
                } else {
                    albumGateway.getTrackListById(mediaId.id)
                }
            }
            MediaIdCategory.ARTISTS -> {
                if (mediaId.isPodcast) {
                    podcastArtistGateway.getTrackListByParam(mediaId.id)
                } else {
                    artistGateway.getTrackListById(mediaId.id)
                }
            }
            MediaIdCategory.GENRES -> genreGateway.getTrackListById(mediaId.id)
            else -> throw AssertionError("invalid media id $mediaId")
        }
    }


}
