package dev.olog.service.music.voice

import dev.olog.core.entity.track.Song
import dev.olog.service.music.model.toMediaEntity
import dev.olog.core.MediaId
import dev.olog.core.entity.track.getMediaId
import dev.olog.core.gateway.GenreGateway
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.rx2.asObservable

object VoiceSearch {

    fun noFilter(flowable: Observable<List<Song>>): Single<List<dev.olog.service.music.model.MediaEntity>> {
        val mediaId = MediaId.songId(-1)
        return flowable.firstOrError()
                .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
    }

    fun filterByAlbum(flowable: Observable<List<Song>>, album: String): Single<List<dev.olog.service.music.model.MediaEntity>> {
        val mediaId = MediaId.songId(-1)
        return flowable.firstOrError()
                .map {
                    it.asSequence()
                            .filter { it.album.equals(album, true) }
                            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
                            .toList()
                }
    }

    fun filterByArtist(flowable: Observable<List<Song>>, artist: String): Single<List<dev.olog.service.music.model.MediaEntity>> {
        val mediaId = MediaId.songId(-1)
        return flowable.firstOrError()
                .map {
                    it.asSequence()
                            .filter { it.artist.equals(artist, true) }
                            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
                            .toList()
                }
    }

    fun filterByTitle(flowable: Observable<List<Song>>, title: String): Single<List<dev.olog.service.music.model.MediaEntity>> {
        val mediaId = MediaId.songId(-1)
        return flowable.firstOrError()
                .map {
                    it.asSequence()
                            .filter { it.title.equals(title, true) }
                            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
                            .toList()
                }
    }

    fun search(flowable: Observable<List<Song>>, param: String): Single<List<dev.olog.service.music.model.MediaEntity>> {
        val mediaId = MediaId.songId(-1)
        return flowable.firstOrError().map {
            it.filter {
                it.title.toLowerCase() == param ||
                        it.artist.toLowerCase() == param ||
                        it.album.toLowerCase() == param
            }
                    .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
        }
    }

    fun filterByGenre(genreGateway: GenreGateway, genre: String): Single<List<dev.olog.service.music.model.MediaEntity>> {

        return genreGateway.observeAll().asObservable()
                .map { it.first { it.name.equals(genre, true) } }
                .firstOrError()
                .flatMap {
                    val mediaId = it.getMediaId()
                    genreGateway.observeTrackListByParam(it.id).asObservable()
                            .firstOrError()
                            .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
                }
    }

}