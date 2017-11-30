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

class BaseListAdapterController<Model>(
        private val adapter: BaseListAdapter<Model>

) : DefaultLifecycleObserver {

    private var dataSetDisposable: Disposable? = null

    val publisher = PublishProcessor.create<AdapterData<MutableList<Model>>>()

    private val originalList = mutableListOf<Model>()
    private val dataSet = mutableListOf<Model>()

    private var dataVersion = 0

    init {
//        dataSet.addAll(0, createActualHeaders())
    }

    operator fun get(position: Int): Model = dataSet[position]

    fun getSize() : Int = dataSet.size

    override fun onStart(owner: LifecycleOwner) {
        dataSetDisposable = publisher
                .toSerialized()
                .onBackpressureLatest()
                .observeOn(Schedulers.computation())
                .map {
                    it.list.addAll(0, adapter.provideHeaders())
                    it
                }
                .filter { it.dataVersion == dataVersion }
                .map {
                    it.to(DiffUtil.calculateDiff(object : DiffUtil.Callback(){

                    init { assertBackgroundThread() }

                    override fun getOldListSize(): Int = dataSet.size

                    override fun getNewListSize(): Int = it.list.size

                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val oldItem : Model = dataSet[oldItemPosition]
                        val newItem : Model = it.list[newItemPosition]
                        return adapter.areItemsTheSame(oldItem, newItem)
                    }

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val oldItem : Model = dataSet[oldItemPosition]
                        val newItem : Model = it.list[newItemPosition]
                        return oldItem == newItem &&
                                oldItemPosition == newItemPosition
                    }
                })) }
                .filter { it.first.dataVersion == dataVersion }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ (newAdapterData, callback) ->

                    val wasEmpty = this.dataSet.isEmpty()

                    this.dataSet.clearThenAdd(newAdapterData.list)

                    if (wasEmpty || !adapter.hasGranularUpdate()) {
                        adapter.notifyDataSetChanged()
                    } else {
                        callback.dispatchUpdatesTo(adapter)
                    }
                    adapter.afterDataChanged()

                }, Throwable::printStackTrace)
    }


    fun onDataChanged(): Flowable<List<Model>> {
        return publisher.map { it.list }
    }

    @CallSuper
    override fun onStop(owner: LifecycleOwner) {
        dataSetDisposable.unsubscribe()
    }

    fun onNext(data: List<Model>) {
        dataVersion++
        this.originalList.clearThenAdd(data)
        publisher.onNext(AdapterData(originalList.toMutableList(), dataVersion))
    }

    fun getDataSet(): List<Model> = dataSet
}