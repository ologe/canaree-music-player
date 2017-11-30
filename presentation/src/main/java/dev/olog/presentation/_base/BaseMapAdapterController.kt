package dev.olog.presentation._base

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.support.v7.util.DiffUtil
import dev.olog.presentation.utils.assertBackgroundThread
import dev.olog.shared.clearThenPut
import dev.olog.shared.unsubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class BaseMapAdapterController <E : Enum<E>, Model> (
        private val adapter: BaseMapAdapter<E, Model>,
        enums: Array<E>

) : DefaultLifecycleObserver {

    private val publisher = PublishProcessor.create<MutableMap<E, MutableList<Model>>>()

    private val originalDataSet : MutableMap<E, MutableList<Model>> = mutableMapOf()
    private val dataSet : MutableMap<E, MutableList<Model>> = mutableMapOf()

    private var dataSetDisposable: Disposable? = null

    init {
        for (enum in enums) {
            originalDataSet.put(enum, mutableListOf())
        }
        dataSet.putAll(originalDataSet)
    }

    fun getSize(): Int = dataSet.values.sumBy { it.size }

    private fun isEmpty(): Boolean = getSize() == 0

    operator fun get(position: Int): Model = getItem(dataSet, position)

    fun onNext(data: MutableMap<E, MutableList<Model>>) {
//        originalDataSet.clearThenPut(addHeaderByType(data))
//        publisher.onNext(originalDataSet.toMutableMap())
        publisher.onNext(data)
    }

    override fun onStart(owner: LifecycleOwner) {
        dataSetDisposable = publisher
                .toSerialized()
                .debounce(50, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.computation())
                .onBackpressureBuffer()
                .distinctUntilChanged()
                .map { it.to(DiffUtil.calculateDiff(object : DiffUtil.Callback(){

                    init { assertBackgroundThread() }

                    override fun getOldListSize(): Int = dataSet.values.sumBy { it.size }

                    override fun getNewListSize(): Int = it.values.sumBy { it.size }

                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val oldItem = getItem(dataSet, oldItemPosition)
                        val newItem = getItem(it, newItemPosition)
                        return adapter.areItemsTheSame(oldItem, newItem)
                    }

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val oldItem = getItem(dataSet, oldItemPosition)
                        val newItem = getItem(it, newItemPosition)
                        return oldItem == newItem &&
                                oldItemPosition == newItemPosition
                    }
                })) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (newData, callback) ->
                    val wasEmpty = isEmpty()

                    dataSet.clearThenPut(newData)

                    if (wasEmpty){
                        adapter.notifyDataSetChanged()
                    } else{
                        callback.dispatchUpdatesTo(adapter)
                    }

//                    adapter.startTransition() // todo
                }
    }

    override fun onStop(owner: LifecycleOwner) {
        dataSetDisposable.unsubscribe()
    }

    private fun getItem(dataSet: Map<E, List<Model>>, position: Int): Model {
        var totalSize = 0
        for (value in dataSet.values) {
            if (position in totalSize until (totalSize + value.size)){
                val realPosition = position - totalSize
                return value[realPosition]
            } else{
                totalSize += value.size
            }
        }
        throw IllegalArgumentException("invalid position $position")
    }

}