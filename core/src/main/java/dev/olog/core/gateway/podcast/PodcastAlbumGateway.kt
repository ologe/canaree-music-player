package dev.olog.core.gateway.podcast

import dev.olog.core.entity.sort.AllPodcastAlbumsSort
import dev.olog.core.entity.sort.PodcastAlbumEpisodesSort
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface PodcastAlbumGateway {

    fun getAll(): List<Album>
    fun observeAll(): Flow<List<Album>>

    fun getByParam(id: Long): Album?
    fun observeByParam(id: Long): Flow<Album?>

    fun getTrackListByParam(id: Long): List<Song>
    fun observeTrackListByParam(id: Long): Flow<List<Song>>

    fun observeLastPlayed(): Flow<List<Album>>
    suspend fun addLastPlayed(id: Long)

    fun observeRecentlyAdded(): Flow<List<Album>>

    fun observeSiblings(id: Long): Flow<List<Album>>

    fun observeArtistsAlbums(artistId: Long): Flow<List<Album>>

    fun setSort(sort: AllPodcastAlbumsSort)
    fun getSort(): AllPodcastAlbumsSort

    fun setEpisodeSort(sort: PodcastAlbumEpisodesSort)
    fun getEpisodeSort(): PodcastAlbumEpisodesSort
}