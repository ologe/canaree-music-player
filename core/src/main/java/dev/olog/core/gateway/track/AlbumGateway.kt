package dev.olog.core.gateway.track

import dev.olog.core.entity.sort.AlbumSongsSort
import dev.olog.core.entity.sort.AllAlbumsSort
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import kotlinx.coroutines.flow.Flow

interface AlbumGateway {

    fun getAll(): List<Album>
    fun observeAll(): Flow<List<Album>>

    fun getByParam(id: Long): Album?
    fun observeByParam(id: Long): Flow<Album?>

    fun getTrackListByParam(id: Long): List<Song>
    fun observeTrackListByParam(id: Long): Flow<List<Song>>

    fun observeLastPlayed(): Flow<List<Album>>
    suspend fun addLastPlayed(id: Long)

    fun observeRecentlyAdded(): Flow<List<Album>>

    fun observeSiblings(id: Long): Flow<List<Album>>

    fun observeArtistsAlbums(artistId: Id): Flow<List<Album>>

    fun setSort(sort: AllAlbumsSort)
    fun getSort(): AllAlbumsSort

    fun setSongSort(sort: AlbumSongsSort)
    fun getSongSort(): AlbumSongsSort

}