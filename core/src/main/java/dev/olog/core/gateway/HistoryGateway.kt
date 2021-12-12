package dev.olog.core.gateway

import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface HistoryGateway {

    fun getSongs(): List<Song>
    fun getPodcastEpisodes(): List<Song>

    fun observeSongs(): Flow<List<Song>>
    fun observePodcastEpisodes(): Flow<List<Song>>

}