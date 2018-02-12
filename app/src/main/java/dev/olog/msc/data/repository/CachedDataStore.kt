package dev.olog.msc.data.repository

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class CachedDataStore<T> {

    private val publisher = BehaviorSubject.createDefault<List<T>>(listOf())

    fun updateCache(list: List<T>) {
        if (list != publisher.value){
            publisher.onNext(list)
        }
    }

    fun isEmpty() = publisher.value.isEmpty()

    fun getAll(): Observable<List<T>> = publisher
            .take(1)
            .map { it.toList() }

}