package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.support.v7.util.DiffUtil
import dev.olog.presentation.DetailDiff
import dev.olog.presentation._base.IAdapterController
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.cleanThenAdd
import dev.olog.shared.clearThenPut
import dev.olog.shared.unsubscribe
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class DetailDataController (
        private val adapter: DetailAdapter

) : DefaultLifecycleObserver, IAdapterController<MutableMap<DetailDataType, MutableList<DisplayableItem>>> {

    lateinit var detailHeaders : DetailHeaders

    private var dataSetDisposable: Disposable? = null

    private val publisher = PublishProcessor.create<MutableMap<DetailDataType, MutableList<DisplayableItem>>>()

    private val originalDataSet : MutableMap<DetailDataType, MutableList<DisplayableItem>> = mutableMapOf()

    init {
        for (value in DetailDataType.values()) {
            originalDataSet.put(value, mutableListOf())
        }
    }

    private val dataSet : MutableMap<DetailDataType, MutableList<DisplayableItem>> = originalDataSet.toMutableMap()

    override fun getSize(): Int = dataSet.values.sumBy { it.size }

    override operator fun get(position: Int): DisplayableItem {
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

    override fun onStart(owner: LifecycleOwner) {
        dataSetDisposable = publisher
                .toSerialized()
                .debounce(50, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.computation())
                .onBackpressureBuffer()
                .distinctUntilChanged()
                .map { it.to(DiffUtil.calculateDiff(DetailDiff(dataSet, it))) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (newData, callback) ->
                    val wasEmpty = isEmpty()

                    dataSet.clearThenPut(newData)

                    if (wasEmpty){
                        adapter.notifyDataSetChanged()
                    } else{
                        callback.dispatchUpdatesTo(adapter)
                    }
                    adapter.startTransition()
                }
    }

    override fun onStop(owner: LifecycleOwner) {
        dataSetDisposable.unsubscribe()
    }

    override fun onNext(data: MutableMap<DetailDataType, MutableList<DisplayableItem>>) {
        originalDataSet.clearThenPut(addHeaderByType(data))
        publisher.onNext(originalDataSet.toMutableMap())
    }

    override fun onDataChanged(): Flowable<MutableMap<DetailDataType, MutableList<DisplayableItem>>> {
        return publisher
    }

    override fun getDataSet(): MutableMap<DetailDataType, MutableList<DisplayableItem>> {
        return dataSet
    }

    private fun isEmpty(): Boolean = getSize() == 0

    private fun addHeaderByType(data :MutableMap<DetailDataType, MutableList<DisplayableItem>>)
            : MutableMap<DetailDataType, MutableList<DisplayableItem>> {

        for ((key, value) in data.entries) {
            when (key){
                DetailDataType.MOST_PLAYED -> {
                    if (value.isNotEmpty()){
                        value.clear() // all list is not needed, just add a nested list
                        value.addAll(0, detailHeaders.mostPlayed)
                    }
                }
                DetailDataType.RECENT -> {
                    if (value.isNotEmpty()){
                        value.clear() // all list is not needed, just add a nested list
                        if (value.size > 10){
                            value.addAll(0, detailHeaders.recentWithSeeAll)
                        } else {
                            value.addAll(0, detailHeaders.recent)
                        }
                    }
                }
                DetailDataType.ALBUMS -> {
                    if (value.isNotEmpty()) {
                        val newList = value.take(4).toMutableList()
                        if (value.size > 4){
                            newList.add(0, detailHeaders.albumsWithSeeAll)
                        } else{
                            newList.add(0, detailHeaders.albums)
                        }
                        value.cleanThenAdd(newList)
                    }
                }
                DetailDataType.SONGS -> {
                    if (value.isNotEmpty()){
                        value.addAll(0, detailHeaders.songs)
                    }
                }
                DetailDataType.ARTISTS_IN -> {
                    if (value.isNotEmpty()){
                        val (_, _, title) = value[0]
                        if (title == ""){
                            value.clear()
                        }
                    }
                }
            }
        }

        return data
    }

}