package dev.olog.presentation._base

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.support.annotation.CallSuper
import android.support.v7.util.DiffUtil
import dev.olog.presentation.Diff
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.Header
import dev.olog.presentation.model.toDisplayableItem
import dev.olog.shared.cleanThenAdd
import dev.olog.shared.unsubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

internal class BaseAdapterController(
        private val adapter: BaseAdapter
) : DefaultLifecycleObserver {

    private var dataSetDisposable: Disposable? = null

    private val publisher = PublishProcessor.create<AdapterData<MutableList<DisplayableItem>>>()

    private val originalList = mutableListOf<DisplayableItem>()
    val dataSet = mutableListOf<DisplayableItem>()

    private var dataVersion = 0

    init {
        dataSet.addAll(0, createActualHeaders())
    }

    operator fun get(position: Int): DisplayableItem = dataSet[position]

    fun getSize() : Int = dataSet.size

    fun updateData(newData: List<DisplayableItem>){
        dataVersion++
        this.originalList.cleanThenAdd(newData)
        publisher.onNext(AdapterData(originalList.toMutableList(), dataVersion))
    }

    val onDataChanged = publisher
            .toSerialized()
            .observeOn(Schedulers.computation())
            .onBackpressureLatest()
            .debounce(50, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .replay(1)
            .refCount()

    override fun onStart(owner: LifecycleOwner) {
        dataSetDisposable = onDataChanged
                .observeOn(Schedulers.computation())
                .map {
                    it.list.addAll(0, createActualHeaders())
                    it
                }
                .filter { it.dataVersion == dataVersion }
                .map { it.to(DiffUtil.calculateDiff(Diff(dataSet, it.list))) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ (newAdapterData, callback) ->

                    if (newAdapterData.dataVersion == dataVersion){
                        val wasEmpty = this.dataSet.isEmpty()

                        this.dataSet.cleanThenAdd(newAdapterData.list)

                        if (wasEmpty || !adapter.hasGranularUpdate()) {
                            adapter.notifyDataSetChanged()
                        } else {
                            callback.dispatchUpdatesTo(adapter)
                        }
                    }

                }, Throwable::printStackTrace)
    }

    @CallSuper
    override fun onStop(owner: LifecycleOwner) {
        dataSetDisposable.unsubscribe()
    }

    private fun createActualHeaders(): List<DisplayableItem> {
        return adapter.provideHeaders().mapIndexed { index: Int, header: Header ->
            header.toDisplayableItem(index)
        }
    }

}