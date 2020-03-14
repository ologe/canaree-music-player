package dev.olog.data.repository.track

import dev.olog.core.entity.PureUri
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.data.repository.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class TrackRepository @Inject constructor(

) : TrackGateway {

    override fun getAllTracks(): List<Song> {
        return MockData.songs(false)
    }

    override fun getAllPodcasts(): List<Song> {
        return MockData.songs(true)

    }

    override fun getByParam(param: Long): Song? {
        return (getAllPodcasts() + getAllPodcasts())
            .find { it.id == param }
    }

    override fun observeAllTracks(): Flow<List<Song>> {
        return flowOf(getAllTracks())
    }

    override fun observeAllPodcasts(): Flow<List<Song>> {
        return flowOf(getAllPodcasts())
    }

    override suspend fun deleteSingle(id: Long) {

    }

    override suspend fun deleteGroup(ids: List<Long>) {

    }

    override fun getByAlbumId(albumId: Long): Song? {
        return (getAllTracks() + getAllPodcasts()).find { it.albumId == albumId }
    }

    override fun observeByParam(param: Long): Flow<Song?> {
        return flowOf(getByParam(param))
    }

    override fun getByUri(uri: PureUri): Song? {
        return null
    }
}