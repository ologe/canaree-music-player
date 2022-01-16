package dev.olog.core.history

import dev.olog.core.MediaUri
import dev.olog.core.track.Song
import kotlinx.coroutines.flow.Flow

interface HistoryGateway {

    fun getSongs(): List<Song>
    fun getPodcastEpisodes(): List<Song>

    fun observeSongs(): Flow<List<Song>>
    fun observePodcastEpisodes(): Flow<List<Song>>

    suspend fun insert(uri: MediaUri)

}