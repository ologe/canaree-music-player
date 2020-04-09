package dev.olog.data.repository.podcast

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.data.repository.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class PodcastAuthorRepository @Inject constructor(

) : PodcastAuthorGateway {

    override fun getAll(): List<Artist> {
        return MockData.artist(true)
    }

    override fun observeAll(): Flow<List<Artist>> {
        return flowOf(getAll())
    }

    override fun getByParam(param: Long): Artist? {
        return getAll().first()
    }

    override fun observeByParam(param: Long): Flow<Artist?> {
        return flowOf(getByParam(param))
    }

    override fun observeLastPlayed(): Flow<List<Artist>> {
        return observeAll()
    }

    override suspend fun addLastPlayed(id: Long) {

    }

    override fun observeRecentlyAdded(): Flow<List<Artist>> {
        return observeAll()
    }

    override fun getTrackListByParam(param: Long): List<Song> {
        return MockData.songs(true)
    }

    override fun observeTrackListByParam(param: Long): Flow<List<Song>> {
        return flowOf(getTrackListByParam(param))
    }

    override fun observeSiblings(param: Long): Flow<List<Artist>> {
        return observeAll()
    }
}