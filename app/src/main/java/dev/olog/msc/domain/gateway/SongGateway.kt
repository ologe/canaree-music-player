package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.Song
import io.reactivex.Completable
import io.reactivex.Observable

interface SongGateway : BaseGateway<Song, Long> {

    fun getAllUnfiltered(): Observable<List<Song>>

    fun deleteSingle(songId: Long): Completable

    fun deleteGroup(songList: List<Song>): Completable

    fun getUneditedByParam(songId: Long): Observable<Song>

}