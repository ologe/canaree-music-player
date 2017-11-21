package dev.olog.domain.gateway

import io.reactivex.Completable
import io.reactivex.Flowable

interface HasLastPlayed<T> {

    fun getLastPlayed(): Flowable<List<T>>

    fun addLastPlayed(item: T): Completable

}