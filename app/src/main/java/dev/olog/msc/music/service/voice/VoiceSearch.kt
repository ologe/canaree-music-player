package dev.olog.msc.music.service.voice

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.music.service.model.toMediaEntity
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import io.reactivex.Single

object VoiceSearch {

    fun noFilter(flowable: Observable<List<Song>>): Single<List<MediaEntity>> {
        val mediaId = MediaId.songId(-1, false)
        return flowable.firstOrError()
                .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
    }

    fun filterByAlbum(flowable: Observable<List<Song>>, album: String): Single<List<MediaEntity>> {
        val mediaId = MediaId.songId(-1, false)
        return flowable.firstOrError()
                .map {
                    it.asSequence()
                            .filter { it.album.equals(album, true) }
                            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
                            .toList()
                }
    }

    fun filterByArtist(flowable: Observable<List<Song>>, artist: String): Single<List<MediaEntity>> {
        val mediaId = MediaId.songId(-1, false)
        return flowable.firstOrError()
                .map {
                    it.asSequence()
                            .filter { it.artist.equals(artist, true) }
                            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
                            .toList()
                }
    }

    fun filterByTitle(flowable: Observable<List<Song>>, title: String): Single<List<MediaEntity>> {
        val mediaId = MediaId.songId(-1, false)
        return flowable.firstOrError()
                .map {
                    it.asSequence()
                            .filter { it.title.equals(title, true) }
                            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
                            .toList()
                }
    }

    fun search(flowable: Observable<List<Song>>, param: String): Single<List<MediaEntity>> {
        val mediaId = MediaId.songId(-1, false)
        return flowable.firstOrError().map {
            it.filter { it.title.toLowerCase() == param ||
                    it.artist.toLowerCase() == param ||
                    it.album.toLowerCase() == param }
                    .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
        }
    }

    fun filterByGenre(genreGateway: GenreGateway, genre: String): Single<List<MediaEntity>> {

        return genreGateway.getAll().map { it.first { it.name.equals(genre, true) } }
                .firstOrError()
                .flatMap {
                    val mediaId = MediaId.genreId(it.id)
                    genreGateway.observeSongListByParam(it.id)
                        .firstOrError()
                        .map { it.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) } }
                }
    }

}