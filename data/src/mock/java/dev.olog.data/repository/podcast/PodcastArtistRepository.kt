package dev.olog.data.repository.podcast

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.data.repository.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class PodcastArtistRepository @Inject constructor(): PodcastArtistGateway {

    override fun getAll(): List<Artist> {
        return MockData.artist(true)
    }

    override fun observeAll(): Flow<List<Artist>> {
        return flowOf(getAll())
    }

    override fun getByParam(param: Id): Artist? {
        return getAll().first()
    }

    override fun observeByParam(param: Id): Flow<Artist?> {
        return flowOf(getByParam(param))
    }

    override fun observeLastPlayed(): Flow<List<Artist>> {
        return observeAll()
    }

    override suspend fun addLastPlayed(id: Id) {

    }

    override fun observeRecentlyAdded(): Flow<List<Artist>> {
        return observeAll()
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        return MockData.songs(true)
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        return flowOf(getTrackListByParam(param))
    }

    override fun observeSiblings(param: Id): Flow<List<Artist>> {
        return observeAll()
    }
}