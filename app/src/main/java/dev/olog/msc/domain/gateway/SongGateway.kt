package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.entity.UneditedSong
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface SongGateway : BaseGateway<Song, Long> {

    fun getAllForImageCreation(): Single<List<Song>>
    fun getAllUnfiltered(): Flowable<List<Song>>

    fun deleteSingle(songId: Long): Completable

    fun deleteGroup(songList: List<Song>): Completable

    fun getByParamUnedited(songId: Long): Flowable<UneditedSong>

}