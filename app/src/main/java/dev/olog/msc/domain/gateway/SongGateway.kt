package dev.olog.msc.domain.gateway

import android.net.Uri
import dev.olog.core.entity.track.Song
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface SongGateway : BaseGateway<Song, Long> {

    fun getByAlbumId(albumId: Long): Observable<Song>

    fun getAllUnfiltered(): Observable<List<Song>>

    fun deleteSingle(songId: Long): Completable

    fun deleteGroup(songList: List<Song>): Completable

    fun getUneditedByParam(songId: Long): Observable<Song>

    fun getByUri(uri: Uri): Single<Song>

}