package dev.olog.presentation._base

import android.arch.lifecycle.LifecycleObserver
import dev.olog.presentation.model.DisplayableItem
import io.reactivex.Flowable

interface IAdapterController <T>: LifecycleObserver{

    fun onNext(data: T)

    fun getSize(): Int

    operator fun get(position: Int): DisplayableItem

    fun getDataSet(): T

    fun onDataChanged() : Flowable<T>

}