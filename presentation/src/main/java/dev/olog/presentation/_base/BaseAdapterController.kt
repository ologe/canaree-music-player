package dev.olog.presentation._base

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.support.annotation.CallSuper
import android.support.v7.util.DiffUtil
import dev.olog.presentation.Diff
import dev.olog.presentation.model.DisplayableItem
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
    private val dataSet = mutableListOf<DisplayableItem>()

    private var filter = ""

    operator fun get(position: Int): DisplayableItem = dataSet[position]

    fun getSize() : Int = dataSet.size

    fun updateData(newData: List<DisplayableItem>){
        this.originalList.cleanThenAdd(newData)
        publisher.onNext(originalList.toList())
    }

    private val onDataChanged = publisher
            .toSerialized()
            .observeOn(Schedulers.computation())
            .onBackpressureLatest()
            .debounce(50, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .flatMapSingle {
                if (filter.length >= 2){
                    it.toFlowable()
                            .filter { containsLowerCase(it, filter) }
                            .toList()
                } else {
                    it.toSingle()
                }
            }
            .flatMapSingle { it.toFlowable().toSortedList(compareBy { it.title }) }
            .map { it.to(DiffUtil.calculateDiff(Diff(dataSet, it))) }
            .observeOn(AndroidSchedulers.mainThread())

    override fun onStart(owner: LifecycleOwner) {
        dataSetDisposable = onDataChanged
                .subscribe({ (newData, callback) ->
                    val wasEmpty = this.dataSet.isEmpty()

                    this.dataSet.cleanThenAdd(newData)

                    if (wasEmpty) {
                        adapter.notifyDataSetChanged()
                    } else {
                        callback.dispatchUpdatesTo(adapter)
                    }

                    adapter.afterDataChanged()

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

}