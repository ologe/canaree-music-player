package dev.olog.data.repository.track

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.data.repository.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FolderRepository @Inject constructor(

) : FolderGateway {

    override fun getAllBlacklistedIncluded(): List<Folder> {
        return MockData.folders()
    }

    override fun getAll(): List<Folder> {
        return MockData.folders()
    }

    override fun observeAll(): Flow<List<Folder>> {
        return flowOf(getAll())
    }

    override fun getByParam(param: Long): Folder? {
        return MockData.folders().first()
    }

    override fun observeByParam(param: Long): Flow<Folder?> {
        return flowOf(getByParam(param))
    }

    override fun getTrackListByParam(param: Long): List<Song> {
        return MockData.songs(false)
    }

    override fun observeTrackListByParam(param: Long): Flow<List<Song>> {
        return flowOf(getTrackListByParam(param))
    }

    override fun observeMostPlayed(mediaId: MediaId.Category): Flow<List<Song>> {
        return flowOf(MockData.songs(false))
    }

    override suspend fun insertMostPlayed(mediaId: MediaId.Track) {

    }

    override fun observeSiblings(param: Long): Flow<List<Folder>> {
        return observeAll()
    }

    override fun observeRelatedArtists(param: Long): Flow<List<Artist>> {
        return flowOf(MockData.artist(false))
    }

    override fun observeRecentlyAdded(param: Long): Flow<List<Song>> {
        return observeTrackListByParam(param)
    }
}