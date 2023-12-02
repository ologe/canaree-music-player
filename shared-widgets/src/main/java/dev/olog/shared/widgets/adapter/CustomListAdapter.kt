package dev.olog.shared.widgets.adapter

import androidx.annotation.CallSuper
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView

/**
 * Custom implementation of [androidx.recyclerview.widget.ListAdapter] to allows
 * move and remove operations
 */
abstract class CustomListAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    diffCallback: ItemCallback<T>,
) : RecyclerView.Adapter<VH>() {

    private val differ = CustomAsyncListDiffer<T>(
        AdapterListUpdateCallback(this),
        AsyncDifferConfig.Builder(diffCallback).build()
    )

    @CallSuper
    open fun submitList(list: List<T>) {
        differ.submitList(list)
    }

    fun getItem(position: Int): T = differ.currentList.get(position)
    override fun getItemCount(): Int = differ.currentList.size
    fun indexOf(predicate: (T) -> Boolean): Int {
        return differ.currentList.indexOfFirst(predicate)
    }

    fun move(from: Int, to: Int) {
        differ.move(from, to)
    }

    fun removeAt(position: Int) {
        differ.remove(position)
    }

}