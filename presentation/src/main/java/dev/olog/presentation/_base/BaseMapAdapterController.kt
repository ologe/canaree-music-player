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

    private var dataSetDisposable: Disposable? = null
    private val publisher = PublishProcessor.create<AdapterData<MutableMap<E, MutableList<Model>>>>()

    private val originalDataSet : MutableMap<E, MutableList<Model>> = mutableMapOf()
    private val dataSet : MutableMap<E, MutableList<Model>> = mutableMapOf()

    private var dataVersion = 0

    init {
        for (enum in enums) {
            originalDataSet.put(enum, mutableListOf())
        }
        dataSet.putAll(originalDataSet)
    }

    operator fun get(position: Int): Model = getItem(dataSet, position)

    fun getSize(): Int = dataSet.values.sumBy { it.size }

    private fun isEmpty(): Boolean = getSize() == 0

    fun onNext(data: MutableMap<E, MutableList<Model>>) {
        dataVersion++
        this.originalDataSet.clearThenPut(data)
        publisher.onNext(AdapterData(originalDataSet.toMutableMap(), dataVersion))
    }

    override fun onStart(owner: LifecycleOwner) {
        dataSetDisposable = publisher
                .toSerialized()
                .observeOn(Schedulers.computation())
                .debounce(50, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .distinctUntilChanged { data -> data.data }
                .filter { it.version == dataVersion }
                .map {

                    it.to(DiffUtil.calculateDiff(object : DiffUtil.Callback(){

                    init { assertBackgroundThread() }

                    override fun getOldListSize(): Int = dataSet.values.sumBy { it.size }

                    override fun getNewListSize(): Int = it.data.values.sumBy { it.size }

                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val oldItem = getItem(dataSet, oldItemPosition)
                        val newItem = getItem(it.data, newItemPosition)
                        return adapter.areItemsTheSame(oldItem, newItem)
                    }

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val oldItem = getItem(dataSet, oldItemPosition)
                        val newItem = getItem(it.data, newItemPosition)
                        return oldItem == newItem
                    }
                })) }
                .filter { it.first.version == dataVersion }
                .map { (data, callback) -> Pair(data.data, callback) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (newData, callback) ->
                    val wasEmpty = isEmpty()

                    dataSet.clearThenPut(newData)

                    if (wasEmpty || !adapter.hasGranularUpdate){
                        adapter.notifyDataSetChanged()
                    } else{
                        callback.dispatchUpdatesTo(adapter)
                    }
                    adapter.afterDataChanged()
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

    fun getDataSet() : MutableMap<E, MutableList<Model>> = dataSet

}