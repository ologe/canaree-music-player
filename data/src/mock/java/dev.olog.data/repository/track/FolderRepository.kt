package dev.olog.data.repository.track

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Path
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.data.repository.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FolderRepository @Inject constructor(): FolderGateway {

    override fun getAllBlacklistedIncluded(): List<Folder> {
        return MockData.folders()
    }

    override fun getByHashCode(hashCode: Int): Folder? {
        return MockData.folders().first()
    }

    override fun getAll(): List<Folder> {
        return MockData.folders()
    }

    override fun observeAll(): Flow<List<Folder>> {
        return flowOf(getAll())
    }

    override fun getByParam(param: Path): Folder? {
        return MockData.folders().first()
    }

    override fun observeByParam(param: Path): Flow<Folder?> {
        return flowOf(getByParam(param))
    }

    override fun getTrackListByParam(param: Path): List<Song> {
        return MockData.songs(false)
    }

    override fun observeTrackListByParam(param: Path): Flow<List<Song>> {
        return flowOf(getTrackListByParam(param))
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>> {
        return flowOf(MockData.songs(false))
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) {

    }

    override fun observeSiblings(param: Path): Flow<List<Folder>> {
        return observeAll()
    }

    override fun observeRelatedArtists(params: Path): Flow<List<Artist>> {
        return flowOf(MockData.artist(false))
    }

    override fun observeRecentlyAdded(path: Path): Flow<List<Song>> {
        return observeTrackListByParam(path)
    }
}