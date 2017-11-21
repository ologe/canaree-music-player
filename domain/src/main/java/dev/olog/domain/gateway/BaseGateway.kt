package dev.olog.domain.gateway

import io.reactivex.Flowable


interface BaseGateway<T, in Params> {

    fun getAll(): Flowable<List<T>>

    fun getByParam(param: Params): Flowable<T>

}

