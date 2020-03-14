package dev.olog.data.repository.track

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.data.repository.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GenreRepository @Inject constructor(
    
): GenreGateway {

    override fun getAll(): List<Genre> {
        return MockData.genre()
    }

    override fun observeAll(): Flow<List<Genre>> {
        return flowOf(getAll())
    }

    override fun getByParam(param: Long): Genre? {
        return MockData.genre().first()
    }

    override fun observeByParam(param: Long): Flow<Genre?> {
        return flowOf(getByParam(param))
    }

    override fun getTrackListByParam(param: Long): List<Song> {
        return MockData.songs(false)
    }

    override fun observeTrackListByParam(param: Long): Flow<List<Song>> {
        return flowOf(getTrackListByParam(param))
    }

    override fun observeMostPlayed(mediaId: MediaId.Category): Flow<List<Song>> {
        return observeTrackListByParam(mediaId.categoryId)
    }

    override suspend fun insertMostPlayed(mediaId: MediaId.Track) {

    }

    override fun observeSiblings(param: Long): Flow<List<Genre>> {
        return observeAll()
    }

    override fun observeRelatedArtists(param: Long): Flow<List<Artist>> {
        return flowOf(MockData.artist(false))
    }

    override fun observeRecentlyAdded(param: Long): Flow<List<Song>> {
        return observeTrackListByParam(param)
    }
}