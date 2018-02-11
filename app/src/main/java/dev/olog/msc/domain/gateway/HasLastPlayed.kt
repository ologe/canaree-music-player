package dev.olog.msc.domain.gateway

import io.reactivex.Completable
import io.reactivex.Observable

interface HasLastPlayed<T> {

    fun getLastPlayed(): Observable<List<T>>

    fun addLastPlayed(item: T): Completable

}