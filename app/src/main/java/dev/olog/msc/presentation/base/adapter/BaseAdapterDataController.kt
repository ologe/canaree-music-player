package dev.olog.msc.presentation.base.adapter

import android.support.v7.util.DiffUtil
import dev.olog.msc.presentation.base.BaseModel
import dev.olog.msc.utils.assertBackgroundThread
import dev.olog.msc.utils.k.extension.swap
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.cast
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

/**
 * Adapter controller that can handle [LinkedHashMap] and [List] of type [Model].
 * For map LinkedHashMap is required to preserve original order.
 * @throws RuntimeException if passed data is not [List] nor [LinkedHashMap]
 */
class BaseAdapterDataController<Model : BaseModel>
    : AdapterDataController<Model> {

    private val data = mutableListOf<Model>()
    private val publisher = BehaviorSubject.create<Any>()

    private val valvePublisher = BehaviorSubject.createDefault(true)
    private var valveDisposable : Disposable? = null

    override fun pauseObservingData(){
        valvePublisher.onNext(false)
    }

    override fun resumeObservingData(instantly: Boolean){
        if (instantly){
            valvePublisher.onNext(true)
            return
        }

        valveDisposable.unsubscribe()
        valveDisposable = Single.timer(1, TimeUnit.SECONDS)
                .subscribe({
                    valvePublisher.onNext(true)
                }, Throwable::printStackTrace)
    }

    override fun handleNewData(extendAreItemTheSame: ((Int, Int, Model, Model) -> Boolean)?)
            : Observable<AdapterControllerResult> {

        return Observables.combineLatest(
                publisher.serialize(),
                valvePublisher.distinctUntilChanged()
        ) { data, valve -> data to valve }
                .filter { it.second }
                .map { it.first }
                .distinctUntilChanged()
                .observeOn(Schedulers.computation())
                .doOnNext { assertBackgroundThread() }
                .flatMapSingle {
                    when (it){
                        is BaseModel -> Single.just(listOf(it))
                        is List<*> -> Single.just(it)
                        is Map<*,*> -> {
                            @Suppress("UNCHECKED_CAST")
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