package dev.olog.msc.presentation.edit.artist

import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.UsedImageGateway
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.item.GetArtistUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Single
import javax.inject.Inject

class EditArtistFragmentPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val getArtistUseCase: GetArtistUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val usedImageGateway: UsedImageGateway

) {

    private lateinit var originalArtist: Artist
    lateinit var songList: List<Song>

    fun observeArtist(): Single<Artist> {
        return getArtistUseCase.execute(mediaId)
                .firstOrError()
                .doOnSuccess {
                    val image = usedImageGateway.getForArtist(it.id)
                    originalArtist = it.copy(image = image ?: "")
                }
    }

    fun getSongList(): Single<List<Song>> {
        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .doOnSuccess { songList = it }
    }

    fun getAlbum(): Artist = originalArtist

}