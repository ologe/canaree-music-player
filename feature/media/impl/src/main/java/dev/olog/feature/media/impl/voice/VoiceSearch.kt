package dev.olog.feature.media.impl.voice

import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.feature.media.impl.model.MediaEntity
import dev.olog.feature.media.impl.model.toMediaEntity

object VoiceSearch {

    fun noFilter(songList: List<Song>): List<MediaEntity> {
        return songList.mapIndexed { index, song -> song.toMediaEntity(index, null) }
    }

    fun filterByAlbum(songList: List<Song>, query: String): List<MediaEntity> {
        return songList.asSequence()
            .filter { it.album.equals(query, true) }
            .mapIndexed { index, song -> song.toMediaEntity(index, null) }
            .toList()
    }

    fun filterByArtist(songList: List<Song>, query: String): List<MediaEntity> {
        return songList.asSequence()
            .filter { it.artist.equals(query, true) }
            .mapIndexed { index, song -> song.toMediaEntity(index, null) }
            .toList()
    }

    fun filterByTrack(songList: List<Song>, query: String): List<MediaEntity> {
        return songList.asSequence()
            .filter { it.title.equals(query, true) }
            .mapIndexed { index, song -> song.toMediaEntity(index, null) }
            .toList()
    }

    fun search(songList: List<Song>, query: String): List<MediaEntity> {
        return songList.asSequence()
            .filter {
                it.title.equals(query, true) ||
                        it.artist.equals(query, true) ||
                        it.album.equals(query, true)
            }
            .mapIndexed { index, song -> song.toMediaEntity(index, null) }
            .toList()
    }

    fun filterByGenre(genreGateway: GenreGateway, query: String): List<MediaEntity> {
        val genre = genreGateway.getAll().find { it.name == query } ?: return emptyList()
        val parentMediaId = genre.getMediaId()

        return genreGateway.getTrackListById(genre.id)
            .mapIndexed { index, song -> song.toMediaEntity(index, parentMediaId) }
    }

}