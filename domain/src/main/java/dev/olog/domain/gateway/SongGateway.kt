package dev.olog.domain.gateway

import dev.olog.domain.entity.Song
import io.reactivex.Completable
import io.reactivex.Single

interface SongGateway : BaseGateway<Song, Long> {

    fun getAllForImageCreation(): Single<List<Song>>

    fun deleteSingle(songId: Long): Completable

    fun deleteGroup(songList: List<Song>): Completable

}