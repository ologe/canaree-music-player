package dev.olog.presentation.edit.album

import dev.olog.domain.entity.track.Album
import dev.olog.domain.gateway.track.AlbumGateway
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.intents.AppConstants
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditAlbumFragmentPresenter @Inject constructor(
    private val albumGateway: AlbumGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val schedulers: Schedulers

) {

    fun getAlbum(mediaId: PresentationId.Category): Album {
        val album = albumGateway.getByParam(mediaId.categoryId.toLong())!!
        return Album(
            id = album.id,
            artistId = album.artistId,
            albumArtist = album.albumArtist,
            title = album.title,
            artist = if (album.artist == AppConstants.UNKNOWN) "" else album.artist,
            hasSameNameAsFolder = album.hasSameNameAsFolder,
            songs = album.songs
        )
    }

    suspend fun getPath(mediaId: PresentationId.Category): String = withContext(schedulers.io) {
        getSongListByParamUseCase(mediaId.toDomain()).first().path
    }

}