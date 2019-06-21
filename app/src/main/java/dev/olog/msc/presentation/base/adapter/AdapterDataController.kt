package dev.olog.msc.presentation.base.adapter

import dev.olog.presentation.model.BaseModel
import io.reactivex.Observable

interface AdapterDataController<Model: BaseModel>{

    fun getItem(position: Int): Model?
    fun getSize(): Int
    fun update(data: Any)
    fun handleNewData(extendAreItemTheSame: ((Int, Int, Model, Model) -> Boolean)?): Observable<AdapterControllerResult>

    fun swap(from: Int, to: Int)
    fun remove(position: Int)

    fun getAll(): List<Model>

    fun pauseObservingData()
    fun resumeObservingData(instantly: Boolean)

}