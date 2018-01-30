package dev.olog.music_service.voice

import dev.olog.domain.entity.Song
import dev.olog.music_service.model.MediaEntity
import dev.olog.music_service.model.toMediaEntity
import dev.olog.shared.MediaId
import io.reactivex.Flowable
import io.reactivex.Single

object VoiceSearch {

    fun noFilter(flowable: Flowable<List<Song>>): Single<List<MediaEntity>> {
        val mediaId = MediaId.songId(-1)
        return flowable.firstOrError()
                .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
    }

    fun filterByAlbum(flowable: Flowable<List<Song>>, album: String): Single<List<MediaEntity>> {
        val mediaId = MediaId.songId(-1)
        return flowable.firstOrError().map {
                    it.filter { it.album.toLowerCase() == album }
                            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
                }
    }

    fun filterByArtist(flowable: Flowable<List<Song>>, artist: String): Single<List<MediaEntity>> {
        val mediaId = MediaId.songId(-1)
        return flowable.firstOrError().map {
            it.filter { it.artist.toLowerCase() == artist }
                    .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
        }
    }

    fun filterByTitle(flowable: Flowable<List<Song>>, title: String): Single<List<MediaEntity>> {
        val mediaId = MediaId.songId(-1)
        return flowable.firstOrError().map {
            it.filter { it.title.toLowerCase() == title }
                    .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
        }
    }

    fun search(flowable: Flowable<List<Song>>, param: String): Single<List<MediaEntity>> {
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