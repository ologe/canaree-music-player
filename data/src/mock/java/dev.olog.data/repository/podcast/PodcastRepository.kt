package dev.olog.data.repository.podcast

import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.data.repository.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class PodcastRepository @Inject constructor(): PodcastGateway {

    override suspend fun deleteSingle(id: Id) {

    }

    override suspend fun deleteGroup(podcastList: List<Song>) {

    }

    override fun getCurrentPosition(podcastId: Long, duration: Long): Long {
        return 0
    }

    override fun saveCurrentPosition(podcastId: Long, position: Long) {

    }

    override fun getByAlbumId(albumId: Id): Song? {
        return MockData.songs(true).first()
    }

    override fun getAll(): List<Song> {
        return MockData.songs(true)
    }

    override fun observeAll(): Flow<List<Song>> {
        return flowOf(getAll())
    }

    override fun getByParam(param: Id): Song? {
        return MockData.songs(true).first()
    }

    override fun observeByParam(param: Id): Flow<Song?> {
        return flowOf(getByParam(param))
    }
}