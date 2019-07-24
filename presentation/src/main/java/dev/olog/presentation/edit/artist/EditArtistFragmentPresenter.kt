package dev.olog.presentation.edit.artist

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.interactor.songlist.ObserveSongListByParamUseCase
import io.reactivex.Single
import javax.inject.Inject

class EditArtistFragmentPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val getArtistUseCase: ArtistGateway,
    private val getPodcastArtistUseCase: PodcastArtistGateway,
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
        TODO()
//        return getArtistUseCase.execute(mediaId)
//                .firstOrError()
//                .map { it.toDisplayableArtist() }
//                .doOnSuccess { originalArtist = it }
    }

    private fun getPodcastArtistInternal(): Single<DisplayableArtist>{
        TODO()
//        return getPodcastArtistUseCase.execute(mediaId)
//                .firstOrError()
//                .map { it.toDisplayableArtist() }
//                .doOnSuccess { originalArtist = it }
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