package dev.olog.data.playing

import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.PlayingGateway
import dev.olog.data.mediastore.song.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlayingRepository @Inject constructor(
    private val dao: PlayingDao,
) : PlayingGateway {

    override fun observe(): Flow<Song?> {
        return dao.observe().map { it?.toDomain() }
    }

    override suspend fun update(id: String) {
        dao.updatePlaying(id)
    }
}