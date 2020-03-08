package dev.olog.presentation.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.olog.presentation.model.BaseModel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

abstract class ObservableAdapter<T : BaseModel>(
    itemCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, DataBoundViewHolder>(itemCallback){

    private val _observeData = ConflatedBroadcastChannel<List<T>>(currentList)
    val observeData: Flow<List<T>> = _observeData.asFlow()

    fun getData(): List<T> = currentList

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

    // keep it to remove platform nullabity
    override fun getItem(position: Int): T {
        return super.getItem(position)
    }

    // expose to external
    fun item(position: Int): T {
        return getItem(position)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        val item = getItem(position)
        bind(holder, item, position)
    }

    override fun onCurrentListChanged(previousList: MutableList<T>, currentList: MutableList<T>) {
        _observeData.offer(currentList)
    }

    protected abstract fun bind(holder: DataBoundViewHolder, item: T, position: Int)

}