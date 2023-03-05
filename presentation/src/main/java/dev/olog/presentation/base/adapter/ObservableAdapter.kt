package dev.olog.presentation.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.model.BaseModel
import dev.olog.shared.android.utils.assertBackgroundThread
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

abstract class ObservableAdapter<T : BaseModel>(
    lifecycle: Lifecycle,
    private val itemCallback: DiffUtil.ItemCallback<T>

) : RecyclerView.Adapter<DataBoundViewHolder>(),
    DefaultLifecycleObserver,
    CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.Default) {

    protected val dataSet = mutableListOf<T>()
    private var neverEmitted = true

    private val channel = ConflatedBroadcastChannel<List<T>>()

    fun getData(): List<T> = dataSet.toList()

    fun observeData(skipInitialValue: Boolean): Flow<List<T>> {
        return flow {
            if (!skipInitialValue && !neverEmitted) {
                // emit first only if has a valid value
                emit(dataSet)
            }
            for (t in channel.openSubscription()) {
                emit(t)
            }
        }
    }

    init {
        lifecycle.addObserver(this)

        launch {
            channel.asFlow()
                .distinctUntilChanged()
                .collect { list ->
                    assertBackgroundThread()
                    val diffCallback = AdapterDiffUtil(dataSet.toList(), list, itemCallback)
                    val diff = DiffUtil.calculateDiff(diffCallback, true)
                    withContext(Dispatchers.Main) {
                        updateDataSetInternal(list)
                        diff.dispatchUpdatesTo(this@ObservableAdapter)
                    }
                }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        channel.close()
        cancel()
    }

    @CallSuper
    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAppear()
    }

    @CallSuper
    override fun onViewDetachedFromWindow(holder: DataBoundViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDisappear()
    }

    fun getItem(position: Int): T? {
        if (position in 0..dataSet.size) {
            return dataSet[position]
        }
        return null
    }

    fun indexOf(predicate: (T) -> Boolean): Int {
        return dataSet.indexOfFirst(predicate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(viewType, parent, false)
        val viewHolder = DataBoundViewHolder(view)
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    protected abstract fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int)

    override fun getItemCount(): Int = dataSet.size

    fun lastIndex(): Int = dataSet.lastIndex

    override fun getItemViewType(position: Int): Int = dataSet[position].type

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        val item = dataSet[position]
        bind(holder, item, position)
    }

    protected abstract fun bind(holder: DataBoundViewHolder, item: T, position: Int)

    fun updateDataSet(data: List<T>) {
        channel.offer(data)
    }

    private fun updateDataSetInternal(data: List<T>) {
        this.dataSet.clear()
        this.dataSet.addAll(data)
        neverEmitted = false
    }

}