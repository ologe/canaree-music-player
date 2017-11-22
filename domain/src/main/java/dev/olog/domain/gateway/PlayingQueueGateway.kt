package dev.olog.domain.gateway

import dev.olog.domain.entity.Song
import io.reactivex.Completable
import io.reactivex.Single

interface PlayingQueueGateway {

    fun getAll(): Single<List<Song>>

    fun update(list: List<Long>): Completable

}