package dev.olog.domain.gateway

import dev.olog.domain.entity.Song
import dev.olog.domain.entity.UneditedSong
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface SongGateway : BaseGateway<Song, Long> {

    fun getAllForImageCreation(): Single<List<Song>>

    fun deleteSingle(songId: Long): Completable

    fun deleteGroup(songList: List<Song>): Completable

    fun getByParamUnedited(songId: Long): Flowable<UneditedSong>

}