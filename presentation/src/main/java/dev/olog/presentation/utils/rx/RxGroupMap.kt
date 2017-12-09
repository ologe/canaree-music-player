package dev.olog.presentation.utils.rx

import android.annotation.SuppressLint
import io.reactivex.Flowable

@SuppressLint("CheckResult")
fun <T, R> Flowable<List<T>>.groupMap(mapper: ((T) -> R)): Flowable<List<R>> {
    return flatMapSingle { Flowable.fromIterable(it).map(mapper).toList() }
}