package dev.olog.presentation.edit.album

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Album
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import dev.olog.intents.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditAlbumFragmentPresenter @Inject constructor(
    private val albumGateway: AlbumGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
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
            artist = if (album.artist == AppConstants.UNKNOWN) "" else album.artist,
            hasSameNameAsFolder = album.hasSameNameAsFolder,
            songs = album.songs,
            isPodcast = album.isPodcast
        )
    }

    suspend fun getPath(mediaId: MediaId): String = withContext(Dispatchers.IO) {
        getSongListByParamUseCase(mediaId).first().path
    }

}