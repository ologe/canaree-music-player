package dev.olog.presentation.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.model.BaseModel
import dev.olog.shared.CustomScope
import dev.olog.shared.utils.assertBackgroundThread
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class ObservableAdapter<T : BaseModel>(
    lifecycle: Lifecycle,
    private val itemCallback: DiffUtil.ItemCallback<T>

) : RecyclerView.Adapter<DataBoundViewHolder>(),
    DefaultLifecycleObserver,
    CoroutineScope by CustomScope() {

    protected val dataSet = mutableListOf<T>()

    private val channel = Channel<List<T>>(Channel.CONFLATED)

    fun getData(): List<T> = dataSet.toList()

    fun observeData(skipInitialValue: Boolean): Flow<List<T>> {
        return flow {
            if (!skipInitialValue) {
                emit(dataSet)
            }
            for (t in channel) {
                emit(t)
            }
        }
    }

    init {
        lifecycle.addObserver(this)
        launch {
            for (list in channel) {
                assertBackgroundThread()
                val diffCallback = AdapterDiffUtil(dataSet.toList(), list, itemCallback)
                yield()
                val diff = DiffUtil.calculateDiff(diffCallback, false)
                yield()
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
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        val viewHolder = DataBoundViewHolder(binding)
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    protected abstract fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int)

    override fun getItemCount(): Int = dataSet.size

    override fun getItemViewType(position: Int): Int = dataSet[position].type

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        val item = dataSet[position]
        bind(holder.binding, item, position)
        holder.binding.executePendingBindings()
    }

    protected abstract fun bind(binding: ViewDataBinding, item: T, position: Int)

    fun updateDataSet(data: List<T>) {
        channel.offer(data)
    }

    private fun updateDataSetInternal(data: List<T>) {
        this.dataSet.clear()
        this.dataSet.addAll(data)
    }

}