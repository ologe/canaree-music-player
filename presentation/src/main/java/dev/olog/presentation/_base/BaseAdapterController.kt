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
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class BaseAdapterController(
        private val adapter: BaseAdapter
) : DefaultLifecycleObserver {

    private var dataSetDisposable: Disposable? = null

    private val publisher = PublishProcessor.create<List<DisplayableItem>>()

    private val originalList = mutableListOf<DisplayableItem>()
    val dataSet = mutableListOf<DisplayableItem>()

    private var filter = ""

    init {
        dataSet.addAll(0, createActualHeaders())
    }

    operator fun get(position: Int): DisplayableItem = dataSet[position]

    fun getSize() : Int = dataSet.size

    fun updateData(newData: List<DisplayableItem>){
        this.originalList.cleanThenAdd(newData)
        publisher.onNext(originalList.toList())
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
                    val result = it.toMutableList()
                    result.addAll(0, createActualHeaders())
                    result.toList()
                }
                .flatMapSingle {
                    if (filter.length >= 2){
                        it.toFlowable()
                                .filter { containsLowerCase(it, filter) }
                                .toList()
                    } else {
                        it.toSingle()
                    }
                }
                .map { it.to(DiffUtil.calculateDiff(Diff(dataSet, it))) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ (newData, callback) ->
                    val wasEmpty = this.dataSet.isEmpty()

                    this.dataSet.cleanThenAdd(newData)

                    if (wasEmpty) {
                        adapter.notifyDataSetChanged()
                    } else {
                        callback.dispatchUpdatesTo(adapter)
                    }

                },  { it.printStackTrace() })
    }

    @CallSuper
    override fun onStop(owner: LifecycleOwner) {
        dataSetDisposable.unsubscribe()
    }

    private fun containsLowerCase(item: DisplayableItem, pattern: String): Boolean {
        val title = item.title
        val subtitle = item.subtitle
        return title.toLowerCase().contains(pattern) ||
                (subtitle?.toLowerCase()?.contains(pattern) ?: false)
    }

    private fun createActualHeaders(): List<DisplayableItem> {
        return adapter.provideHeaders().mapIndexed { index: Int, header: Header ->
            header.toDisplayableItem(index)
        }
    }

}