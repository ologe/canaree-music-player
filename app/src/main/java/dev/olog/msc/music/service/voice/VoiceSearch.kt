package dev.olog.msc.music.service.voice

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.music.service.model.toMediaEntity
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import io.reactivex.Single

object VoiceSearch {

    fun noFilter(flowable: Observable<List<Song>>): Single<List<MediaEntity>> {
        val mediaId = MediaId.songId(-1)
        return flowable.firstOrError()
                .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
    }

    fun filterByAlbum(flowable: Observable<List<Song>>, album: String): Single<List<MediaEntity>> {
        val mediaId = MediaId.songId(-1)
        return flowable.firstOrError().map {
                    it.filter { it.album.toLowerCase() == album }
                            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
                }
    }

    fun filterByArtist(flowable: Observable<List<Song>>, artist: String): Single<List<MediaEntity>> {
        val mediaId = MediaId.songId(-1)
        return flowable.firstOrError().map {
            it.filter { it.artist.toLowerCase() == artist }
                    .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
        }
    }

    fun filterByTitle(flowable: Observable<List<Song>>, title: String): Single<List<MediaEntity>> {
        val mediaId = MediaId.songId(-1)
        return flowable.firstOrError().map {
            it.filter { it.title.toLowerCase() == title }
                    .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
        }
    }

    fun search(flowable: Observable<List<Song>>, param: String): Single<List<MediaEntity>> {
        val mediaId = MediaId.songId(-1)
        return flowable.firstOrError().map {
            it.filter { it.title.toLowerCase() == param ||
                    it.artist.toLowerCase() == param ||
                    it.album.toLowerCase() == param }
                    .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
        }
    }

//    fun filterByGenre(flowable: Flowable<List<Song>>, genre: String): Single<List<MediaEntity>> {
//        val mediaId = MediaId.songId(-1)
//        return flowable.firstOrError().map {
//            it.filter { it.artist == artist }
//                    .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
//        }
//    }

}