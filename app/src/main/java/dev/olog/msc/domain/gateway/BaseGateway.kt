package dev.olog.msc.domain.gateway

import io.reactivex.Observable


interface BaseGateway<T, in Params> {

    fun getAll(): Observable<List<T>>

    fun getByParam(param: Params): Observable<T>

}

