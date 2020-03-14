package dev.olog.data.repository.track

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.data.repository.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ArtistRepository @Inject constructor(

) : ArtistGateway {

    override fun getAll(): List<Artist> {
        return MockData.artist(false)
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

    override fun getTrackListByParam(param: Long): List<Song> {
        return MockData.songs(false)
    }

    override fun observeTrackListByParam(param: Long): Flow<List<Song>> {
        return flowOf(getTrackListByParam(param))
    }

    override fun observeRecentlyAdded(): Flow<List<Artist>> {
        return observeAll()
    }

    override fun observeLastPlayed(): Flow<List<Artist>> {
        return observeAll()
    }

    override suspend fun addLastPlayed(id: Long) {

    }
}