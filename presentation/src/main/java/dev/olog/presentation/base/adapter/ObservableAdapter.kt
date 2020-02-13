package dev.olog.presentation.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.olog.presentation.model.BaseModel

abstract class ObservableAdapter<T : BaseModel>(
    itemCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, DataBoundViewHolder>(itemCallback){

    private val _observeData = MutableLiveData<List<T>>(currentList)
    val observeData: LiveData<List<T>> = _observeData // TODO check if workds

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

    override fun submitList(list: List<T>?) {
        super.submitList(list)
        _observeData.value = list
    }

    override fun submitList(list: List<T>?, commitCallback: Runnable?) {
        super.submitList(list, commitCallback)
        _observeData.value = list
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

    protected abstract fun bind(holder: DataBoundViewHolder, item: T, position: Int)

}