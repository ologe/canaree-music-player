package dev.olog.core.gateway.podcast

import dev.olog.core.entity.sort.AllPodcastArtistsSort
import dev.olog.core.entity.sort.PodcastArtistEpisodesSort
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface PodcastArtistGateway {

    fun getAll(): List<Artist>
    fun observeAll(): Flow<List<Artist>>

    fun getByParam(id: Long): Artist?
    fun observeByParam(id: Long): Flow<Artist?>

    fun getTrackListByParam(id: Long): List<Song>
    fun observeTrackListByParam(id: Long): Flow<List<Song>>

    fun observeRecentlyAdded(): Flow<List<Artist>>

    fun observeLastPlayed(): Flow<List<Artist>>
    suspend fun addLastPlayed(id: Long)

    fun setSort(sort: AllPodcastArtistsSort)
    fun getSort(): AllPodcastArtistsSort

    fun setEpisodeSort(sort: PodcastArtistEpisodesSort)
    fun getEpisodeSort(): PodcastArtistEpisodesSort

}