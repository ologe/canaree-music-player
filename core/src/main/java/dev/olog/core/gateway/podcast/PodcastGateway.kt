package dev.olog.core.gateway.podcast

import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface PodcastGateway {

    fun getAll(): List<Song>
    fun observeAll(): Flow<List<Song>>

    fun getById(id: Long): Song?
    fun observeById(id: Long): Flow<Song?>

    fun getCurrentPosition(podcastId: Long, duration: Long): Long
    fun saveCurrentPosition(podcastId: Long, position: Long)

    @Deprecated(message = "remove")
    fun getByAlbumId(albumId: Long): Song?
}