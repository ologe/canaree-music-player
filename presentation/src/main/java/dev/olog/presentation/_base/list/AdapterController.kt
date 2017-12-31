package dev.olog.presentation._base.list

import android.arch.lifecycle.DefaultLifecycleObserver
import io.reactivex.Flowable

interface AdapterController <DataType, Model> : DefaultLifecycleObserver, TouchBehaviorCapabilities {

    fun setAdapter(adapter: BaseAdapter<*,*>)

    operator fun get(position: Int): Model

    fun getSize(): Int

    fun onDataChanged(): Flowable<DataType>

    fun onNext(data: DataType)

}