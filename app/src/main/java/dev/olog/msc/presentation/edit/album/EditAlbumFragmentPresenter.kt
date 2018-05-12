package dev.olog.msc.presentation.edit.album

import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.UsedImageGateway
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Single
import javax.inject.Inject

class EditAlbumFragmentPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val usedImageGateway: UsedImageGateway

) {

    lateinit var songList: List<Song>
    private lateinit var originalAlbum: Album

    fun getSongList(): Single<List<Song>> {
        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .doOnSuccess { songList = it }
                .doOnSuccess {
                    val song = it[0]
                    val artist = if (song.artist == AppConstants.UNKNOWN) "" else song.artist

                    val image = usedImageGateway.getForAlbum(song.albumId) ?: song.image
                    originalAlbum = Album(song.albumId, song.artistId, song.album, artist, image, -1)
                }
    }

    fun getAlbum(): Album = originalAlbum

}