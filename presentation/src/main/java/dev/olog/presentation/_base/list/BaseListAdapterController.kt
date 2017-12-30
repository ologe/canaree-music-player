package dev.olog.presentation._base.list

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.support.annotation.CallSuper
import android.support.v7.util.DiffUtil
import dev.olog.presentation._base.BaseModel
import dev.olog.shared.clearThenAdd
import dev.olog.shared.swap
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.assertBackgroundThread
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class BaseListAdapterController<Model : BaseModel>(
        private val adapter: BaseListAdapter<Model>

) : DefaultLifecycleObserver, AdapterController<Model>, TouchBehaviorCapabilities {

    private var dataSetDisposable: Disposable? = null
    private val publisher = PublishProcessor.create<AdapterData<MutableList<Model>>>()

    private val originalList = mutableListOf<Model>()
    private val dataSet = mutableListOf<Model>()

    private var dataVersion = 0

    override operator fun get(position: Int): Model = dataSet[position]

    override fun remove(position: Int) {
        dataSet.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    override fun swap(from: Int, to: Int) {
        if (from < to){
            for (position in from until to){
                dataSet.swap(position , position+ 1)
            }
        } else {
            for (position in from downTo to + 1){
                dataSet.swap(position , position - 1)
            }
        }
        adapter.notifyItemMoved(from, to)
    }

    override fun headersWithinList(position: Int, viewType: Int): Int {
        return dataSet.indexOfFirst { it.type == viewType }
    }

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
                    it to DiffUtil.calculateDiff(object : DiffUtil.Callback(){

                        init { assertBackgroundThread() }

                        override fun getOldListSize(): Int = dataSet.size

                        override fun getNewListSize(): Int = it.data.size

                        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                            val oldItem : Model = dataSet[oldItemPosition]
                            val newItem : Model = it.data[newItemPosition]
                            return oldItem.mediaId == newItem.mediaId
                        }

                        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                            val oldItem : Model = dataSet[oldItemPosition]
                            val newItem : Model = it.data[newItemPosition]
                            return oldItem == newItem && adapter.areContentTheSameExtension(
                                    oldItemPosition, newItemPosition, oldItem, newItem)
                        }
                    })
                }
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

                    adapter.onDataChangedListener?.onChanged()

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