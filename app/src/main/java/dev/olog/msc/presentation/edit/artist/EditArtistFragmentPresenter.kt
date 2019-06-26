package dev.olog.msc.presentation.edit.artist

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.msc.domain.interactor.all.ObserveSongListByParamUseCase
import dev.olog.msc.domain.interactor.item.GetArtistUseCase
import dev.olog.msc.domain.interactor.item.GetPodcastArtistUseCase
import io.reactivex.Single
import javax.inject.Inject

class EditArtistFragmentPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val getArtistUseCase: GetArtistUseCase,
    private val getPodcastArtistUseCase: GetPodcastArtistUseCase,
    private val getSongListByParamUseCase: ObserveSongListByParamUseCase

) {

    private lateinit var originalArtist: DisplayableArtist
    lateinit var songList: List<Song>

    fun observeArtist(): Single<DisplayableArtist> {
        if (mediaId.isPodcastArtist){
            return getPodcastArtistInternal()
        }
        return getArtistInternal()
    }

    private fun getArtistInternal(): Single<DisplayableArtist>{
        return getArtistUseCase.execute(mediaId)
                .firstOrError()
                .map { it.toDisplayableArtist() }
                .doOnSuccess { originalArtist = it }
    }

    private fun getPodcastArtistInternal(): Single<DisplayableArtist>{
        return getPodcastArtistUseCase.execute(mediaId)
                .firstOrError()
                .map { it.toDisplayableArtist() }
                .doOnSuccess { originalArtist = it }
    }

    fun getSongList(): Single<List<Song>> {
        TODO()
//        return getSongListByParamUseCase.execute(mediaId)
//                .firstOrError()
//                .doOnSuccess { songList = it }
    }

    fun getArtist(): DisplayableArtist = originalArtist

    private fun Artist.toDisplayableArtist(): DisplayableArtist {
        return DisplayableArtist(
                this.id,
                this.name,
                this.albumArtist
        )
    }

}