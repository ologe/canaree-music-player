package dev.olog.msc.presentation.base.adapter

import android.support.v7.util.DiffUtil
import dev.olog.msc.presentation.base.BaseModel
import dev.olog.msc.utils.assertBackgroundThread
import dev.olog.msc.utils.k.extension.swap
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.cast
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Adapter controller that can handle [LinkedHashMap] and [List] of type [Model].
 * For map LinkedHashMap is required to preserve original order.
 * @throws RuntimeException if passed data is not [List] nor [LinkedHashMap]
 */
class BaseAdapterDataController<Model : BaseModel>
    : AdapterDataController<Model> {

    private val data = mutableListOf<Model>()
    private val publisher = PublishSubject.create<Any>()

    override fun handleNewData(extendAreItemTheSame: ((Int, Int, Model, Model) -> Boolean)?)
            : Observable<AdapterControllerResult> {

        return publisher.serialize()
                .observeOn(Schedulers.computation())
                .doOnNext { assertBackgroundThread() }
                .flatMapSingle {
                    when (it){
                        is BaseModel -> Single.just(listOf(it))
                        is List<*> -> Single.just(it)
                        is Map<*,*> -> {
                            val values = it.values as Collection<List<*>>
                            val result = values.reduce { total, current -> total.plus(current) }
                            Single.just(result)
                        }
                        else -> Single.error(NotImplementedError("can not handle class: ${it::class.java}"))
                    }
                }.cast<List<Model>>()
                .map { it.toList() }
                .distinctUntilChanged()
                .map { calculateDiff(it, extendAreItemTheSame) }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { updateData(it.data) }
                .map { AdapterControllerResult(this.data.isEmpty(), it.diffUtil) }
    }

    private fun calculateDiff(
            list: List<Model>,
            extendAreItemTheSame: ((Int, Int, Model, Model) -> Boolean)?): AdapterData<Model> {

        if (list.size > 400){
            return AdapterData(list, null)
        } else {
            val diffCallback = BaseAdapterDiffUtil(data.toList(), list, extendAreItemTheSame)
            return AdapterData(list, DiffUtil.calculateDiff(diffCallback))
        }
    }

    private fun updateData(newData: List<Model>) {
        this.data.clear()
        this.data.addAll(newData)
    }

    override fun getItem(position: Int): Model = data[position]

    override fun getSize(): Int = data.size

    override fun update(data: Any) {
        publisher.onNext(data)
    }

    override fun swap(from: Int, to: Int) {
        if (from < to){
            for (position in from until to){
                data.swap(position , position + 1)
            }
        } else {
            for (position in from downTo to + 1){
                data.swap(position , position - 1)
            }
        }
    }

    override fun remove(position: Int) {
        data.removeAt(position)
    }

    override fun getAll(): List<Model> = data.toList()

}