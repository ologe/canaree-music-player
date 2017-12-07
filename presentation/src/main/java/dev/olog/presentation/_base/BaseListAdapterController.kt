package dev.olog.presentation._base

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.support.annotation.CallSuper
import android.support.v7.util.DiffUtil
import dev.olog.presentation.utils.assertBackgroundThread
import dev.olog.shared.clearThenAdd
import dev.olog.shared.unsubscribe
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class BaseListAdapterController<Model>(
        private val adapter: BaseListAdapter<Model>

) : DefaultLifecycleObserver {

    private var dataSetDisposable: Disposable? = null
    private val publisher = PublishProcessor.create<AdapterData<MutableList<Model>>>()

    private val originalList = mutableListOf<Model>()
    private val dataSet = mutableListOf<Model>()

    private var dataVersion = 0

    operator fun get(position: Int): Model = dataSet[position]

    fun getSize() : Int = dataSet.size

    fun onNext(data: List<Model>) {
        dataVersion++
        this.originalList.clearThenAdd(data)
        publisher.onNext(AdapterData(originalList.toMutableList(), dataVersion))
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

                    override fun getOldListSize(): Int = dataSet.size

                    override fun getNewListSize(): Int = it.data.size

                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val oldItem : Model = dataSet[oldItemPosition]
                        val newItem : Model = it.data[newItemPosition]
                        return adapter.areItemsTheSame(oldItem, newItem)
                    }

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val oldItem : Model = dataSet[oldItemPosition]
                        val newItem : Model = it.data[newItemPosition]
                        return oldItem == newItem &&
                                oldItemPosition == newItemPosition
                    }
                })) }
                .filter { it.first.version == dataVersion }
                .map { (data, callback) -> Pair(data.data, callback)}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ (newData, callback) ->

                    val wasEmpty = this.dataSet.isEmpty()

                    this.dataSet.clearThenAdd(newData)

                    if (wasEmpty || !adapter.hasGranularUpdate) {
                        adapter.notifyDataSetChanged()
                    } else {
                        callback.dispatchUpdatesTo(adapter)
                    }
                    adapter.afterDataChanged()

                }, Throwable::printStackTrace)
    }


    fun onDataChanged(): Flowable<List<Model>> {
        return publisher.map { it.data.toList() }
                .startWith(dataSet)
    }

    @CallSuper
    override fun onStop(owner: LifecycleOwner) {
        dataSetDisposable.unsubscribe()
    }

    fun getDataSet(): List<Model> = dataSet
}