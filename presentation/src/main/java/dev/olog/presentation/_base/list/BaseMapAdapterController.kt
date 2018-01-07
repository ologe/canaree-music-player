package dev.olog.presentation._base.list

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.support.v7.util.DiffUtil
import dev.olog.presentation._base.BaseModel
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.assertBackgroundThread
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class BaseMapAdapterController <E : Enum<E>, Model: BaseModel> (
        private val enums: Array<E>

) : DefaultLifecycleObserver,
        AdapterController<MutableMap<E, MutableList<Model>>, Model>,
        TouchBehaviorCapabilities {

    lateinit var adapter: BaseMapAdapter<E, Model>

    private var dataSetDisposable: Disposable? = null
    private val publisher = PublishProcessor.create<AdapterData<BetterMap<E, Model>>>()

    private val dataSet : BetterMap<E, Model> = BetterMap(enums)

    private var dataVersion = 0

    override operator fun get(position: Int): Model = dataSet[position]

    override fun getItemPosition(predicate: (Model) -> Boolean): Int {
        return dataSet.indexOfFirst(predicate)
    }

    override fun swap(from: Int, to: Int) {
        if (from < to){
            for (position in from until to){
                dataSet.swap(position, position + 1)
            }
        } else {
            for (position in from downTo to + 1){
                dataSet.swap(position, position - 1)
            }
        }
        adapter.notifyItemMoved(from, to)
    }

    override fun remove(position: Int) {
        dataSet.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    override fun headersWithinList(position: Int, viewType: Int): Int {
        val list = dataSet.getListAtPosition(position)
        return list.indexOfFirst { it.type == viewType }
    }

    override fun getSize(): Int = dataSet.size()

    override fun setAdapter(adapter: BaseAdapter<*,*>) {
        this.adapter = adapter as BaseMapAdapter<E, Model>
    }

    override fun onDataChanged(): Flowable<MutableMap<E, MutableList<Model>>> {
        return publisher.map { it.data.wrappedValue() }
                .startWith(dataSet.wrappedValue())
    }

    override fun onNext(data: MutableMap<E, MutableList<Model>>) {
        dataVersion++
        val newData = BetterMap(enums, data)
        publisher.onNext(AdapterData(newData, dataVersion))
    }

    override fun onStart(owner: LifecycleOwner) {
        dataSetDisposable = publisher
                .toSerialized()
                .observeOn(Schedulers.computation())
                .debounce(50, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .distinctUntilChanged { data -> data.data }
                .filter { it.version == dataVersion }
                .map { newData ->
                    if (newData.data.size() > 400){
                        newData to null
                    } else {
                        newData to DiffUtil.calculateDiff(object : DiffUtil.Callback(){

                            init { assertBackgroundThread() }

                            override fun getOldListSize(): Int = dataSet.size()

                            override fun getNewListSize(): Int = newData.data.size()

                            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                                val oldItem : Model = dataSet[oldItemPosition]
                                val newItem : Model = newData.data[newItemPosition]
                                return oldItem.mediaId == newItem.mediaId
                            }

                            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                                val oldItem : Model = dataSet[oldItemPosition]
                                val newItem : Model = newData.data[newItemPosition]
                                return oldItem == newItem && adapter.areContentTheSameExtension(
                                        oldItemPosition, newItemPosition, oldItem, newItem)
                            }
                        })
                    }


                }
                .filter { it.first.version == dataVersion }
                .map { (data, callback) -> Pair(data.data, callback) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (newData, callback) ->
                    val wasEmpty = dataSet.isEmpty()

                    dataSet.update(newData)

                    if (wasEmpty || !adapter.hasGranularUpdate || callback == null){
                        adapter.notifyDataSetChanged()
                    } else{
                        callback.dispatchUpdatesTo(adapter)
                    }

                    adapter.onDataChangedListener?.onChanged()
                }
    }

    override fun onStop(owner: LifecycleOwner) {
        dataSetDisposable.unsubscribe()
    }

}