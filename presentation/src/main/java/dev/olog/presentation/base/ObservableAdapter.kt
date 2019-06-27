package dev.olog.presentation.base

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
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.drop

abstract class ObservableAdapter<T : BaseModel>(
    lifecycle: Lifecycle,
    private val itemCallback: DiffUtil.ItemCallback<T>

) : RecyclerView.Adapter<DataBoundViewHolder>(),
    DefaultLifecycleObserver,
    CoroutineScope by CustomScope() {

    protected val data = mutableListOf<T>()
    private val channel = ConflatedBroadcastChannel<List<T>>()

    fun observeData(skipInitialValue: Boolean): Flow<List<T>> {
        if (skipInitialValue){
            return channel.asFlow().drop(1)
        }
        return channel.asFlow()
    }

    init {
        lifecycle.addObserver(this)
        launch {
            for (list in channel.openSubscription()) {
                val diffCallback = AdapterDiffUtil(data, list, itemCallback)
                yield()
                val diff = DiffUtil.calculateDiff(diffCallback)
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
        if (position in 0..data.size) {
            return data[position]
        }
        return null
    }

    fun indexOf(predicate: (T) -> Boolean): Int {
        return data.indexOfFirst(predicate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        val viewHolder = DataBoundViewHolder(binding)
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    protected abstract fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int)

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int = data[position].type

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        val item = data[position]
        bind(holder.binding, item, position)
        holder.binding.executePendingBindings()
    }

    protected abstract fun bind(binding: ViewDataBinding, item: T, position: Int)

    fun updateDataSet(data: List<T>) {
        launch { channel.send(data) }
    }

    private fun updateDataSetInternal(data: List<T>) {
        this.data.clear()
        this.data.addAll(data)
    }

}