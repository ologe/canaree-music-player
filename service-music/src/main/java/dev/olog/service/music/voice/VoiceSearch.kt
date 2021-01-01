package dev.olog.service.music.voice

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.track.GenreGateway
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.toMediaEntity
import javax.inject.Inject

// TODO refactor and inject
internal class VoiceSearch @Inject constructor(){

    fun noFilter(songList: List<Track>): List<MediaEntity> {
        val mediaId = MediaId.songId(-1)
        return songList.mapIndexed { index, track -> track.toMediaEntity(index, mediaId) }
    }

    fun filterByAlbum(songList: List<Track>, query: String): List<MediaEntity> {
        val mediaId = MediaId.songId(-1)
        return songList.asSequence()
            .filter { it.album.equals(query, true) }
            .mapIndexed { index, track -> track.toMediaEntity(index, mediaId) }
            .toList()
    }

    fun filterByArtist(songList: List<Track>, query: String): List<MediaEntity> {
        val mediaId = MediaId.songId(-1)
        return songList.asSequence()
            .filter { it.artist.equals(query, true) }
            .mapIndexed { index, track -> track.toMediaEntity(index, mediaId) }
            .toList()
    }

    fun filterByTrack(songList: List<Track>, query: String): List<MediaEntity> {
        val mediaId = MediaId.songId(-1)
        return songList.asSequence()
            .filter { it.title.equals(query, true) }
            .mapIndexed { index, track -> track.toMediaEntity(index, mediaId) }
            .toList()
    }

    fun search(songList: List<Track>, query: String): List<MediaEntity> {
        val mediaId = MediaId.songId(-1)
        return songList.asSequence()
            .filter {
                it.title.equals(query, true) ||
                        it.artist.equals(query, true) ||
                        it.album.equals(query, true)
            }
            .mapIndexed { index, track -> track.toMediaEntity(index, mediaId) }
            .toList()
    }

    suspend fun filterByGenre(genreGateway: GenreGateway, query: String): List<MediaEntity> {
        val genre = genreGateway.getAll().find { it.name == query } ?: return emptyList()
        val parentMediaId = genre.getMediaId()

        return genreGateway.getTrackListByParam(genre.id)
            .mapIndexed { index, track -> track.toMediaEntity(index, parentMediaId) }
    }

}