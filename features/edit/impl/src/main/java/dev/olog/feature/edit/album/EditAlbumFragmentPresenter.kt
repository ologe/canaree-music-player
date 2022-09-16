package dev.olog.feature.edit.album

import android.provider.MediaStore
import dev.olog.core.MediaId
import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.track.Album
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditAlbumFragmentPresenter @Inject constructor(
    private val albumGateway: AlbumGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val lastFmGateway: ImageRetrieverGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase

) {

    fun getAlbum(mediaId: MediaId): Album {
        val album = if (mediaId.isPodcastAlbum) {
            podcastAlbumGateway.getByParam(mediaId.categoryId)!!
        } else {
            albumGateway.getByParam(mediaId.categoryId)!!
        }
        return Album(
            id = album.id,
            artistId = album.artistId,
            albumArtist = album.albumArtist,
            title = album.title,
            artist = if (album.artist == MediaStore.UNKNOWN_STRING) "" else album.artist,
            songs = album.songs,
            isPodcast = album.isPodcast,
            directory = album.directory,
        )
    }

    suspend fun getPath(mediaId: MediaId): String = withContext(Dispatchers.IO) {
        getSongListByParamUseCase(mediaId).first().path
    }

    suspend fun fetchData(id: Long): LastFmAlbum? {
        return lastFmGateway.getAlbum(id)
    }

}