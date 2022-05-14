package dev.olog.feature.media.voice

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.feature.media.api.model.MediaEntity
import dev.olog.feature.media.api.model.toMediaEntity

internal object VoiceSearch {

    fun noFilter(songList: List<Song>): List<MediaEntity> {
        val mediaId = MediaId.songId(-1)
        return songList.mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
    }

    fun filterByAlbum(songList: List<Song>, query: String): List<MediaEntity> {
        val mediaId = MediaId.songId(-1)
        return songList.asSequence()
            .filter { it.album.equals(query, true) }
            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
            .toList()
    }

    fun filterByArtist(songList: List<Song>, query: String): List<MediaEntity> {
        val mediaId = MediaId.songId(-1)
        return songList.asSequence()
            .filter { it.artist.equals(query, true) }
            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
            .toList()
    }

    fun filterByTrack(songList: List<Song>, query: String): List<MediaEntity> {
        val mediaId = MediaId.songId(-1)
        return songList.asSequence()
            .filter { it.title.equals(query, true) }
            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
            .toList()
    }

    fun search(songList: List<Song>, query: String): List<MediaEntity> {
        val mediaId = MediaId.songId(-1)
        return songList.asSequence()
            .filter {
                it.title.equals(query, true) ||
                        it.artist.equals(query, true) ||
                        it.album.equals(query, true)
            }
            .mapIndexed { index, song -> song.toMediaEntity(index, mediaId) }
            .toList()
    }

    fun filterByGenre(genreGateway: GenreGateway, query: String): List<MediaEntity> {
        val genre = genreGateway.getAll().find { it.name == query } ?: return emptyList()
        val parentMediaId = genre.getMediaId()

        return genreGateway.getTrackListByParam(genre.id)
            .mapIndexed { index, song -> song.toMediaEntity(index, parentMediaId) }
    }

}