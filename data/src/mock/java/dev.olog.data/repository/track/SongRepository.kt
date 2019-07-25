package dev.olog.data.repository.track

import android.net.Uri
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.repository.MockData
import io.reactivex.Completable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class SongRepository @Inject constructor(

): SongGateway {

    override fun deleteSingle(id: Id): Completable {
        return Completable.complete()
    }

    override fun deleteGroup(ids: List<Song>): Completable {
        return Completable.complete()
    }

    override fun getByUri(uri: Uri): Song? {
        return null
    }

    override fun getByAlbumId(albumId: Id): Song? {
        return getAll().find { it.albumId == albumId }
    }

    override fun getAll(): List<Song> {
        return MockData.songs(false)
    }

    override fun observeAll(): Flow<List<Song>> {
        return flowOf(getAll())
    }

    override fun getByParam(param: Id): Song? {
        return getAll().find { it.id == param }
    }

    override fun observeByParam(param: Id): Flow<Song?> {
        return flowOf(getByParam(param))
    }
}