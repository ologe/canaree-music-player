package dev.olog.presentation.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.model.BaseModel
import dev.olog.shared.swap
import kotlinx.coroutines.flow.*

@Deprecated(message = "use directly ListAdapter")
abstract class ObservableAdapter<T : BaseModel>(
    itemCallback: DiffUtil.ItemCallback<T>,
) : ListAdapter<T, DataBoundViewHolder>(itemCallback) {

    fun observeChanges(): Flow<List<T>> {
        val flow = MutableStateFlow(currentList)

        val listener = object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                flow.value = currentList
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                flow.value = currentList
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                flow.value = currentList
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                flow.value = currentList
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                flow.value = currentList
            }
        }

        return flow
            .onStart { registerAdapterDataObserver(listener) }
            .onCompletion { unregisterAdapterDataObserver(listener) }
            .debounce(50)
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

    fun indexOf(predicate: (T) -> Boolean): Int {
        return currentList.indexOfFirst(predicate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(viewType, parent, false)
        val viewHolder = DataBoundViewHolder(view)
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    protected abstract fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int)

    fun lastIndex(): Int = currentList.lastIndex

    override fun getItemViewType(position: Int): Int = getItem(position).type

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        val item = getItem(position)
        bind(holder, item, position)
    }

    protected abstract fun bind(holder: DataBoundViewHolder, item: T, position: Int)

    protected fun swap(from: Int, to: Int) {
        val current = currentList.toMutableList()
        current.swap(from, to)
        submitList(current)
    }

    protected fun removeAt(index: Int) {
        val current = currentList.toMutableList()
        current.removeAt(index)
        submitList(current)
    }

    public override fun getItem(position: Int): T {
        return super.getItem(position)
    }

}