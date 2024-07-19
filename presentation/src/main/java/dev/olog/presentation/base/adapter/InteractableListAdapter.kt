package dev.olog.presentation.base.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.olog.shared.swap

abstract class InteractableListAdapter<T, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffCallback) {

    private var isBeingUpdated = false
    private val tmpList = mutableListOf<T>()

    fun moveItem(from: Int, to: Int) {
        if (!isBeingUpdated) {
            tmpList.clear()
            tmpList.addAll(currentList)
        }
        isBeingUpdated = true
        tmpList.swap(from, to)
        notifyItemMoved(from, to)
    }

    fun removeItem(position: Int) {
        if (isBeingUpdated) {
            // exit in case there's already any other update, should not happen
            // but at least avoid inconsistent state
            return
        }
        isBeingUpdated = true
        tmpList.clear()
        tmpList.addAll(currentList)
        tmpList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun commitChange(onDone: (List<T>) -> Unit) {
        if (!isBeingUpdated) {
            return
        }
        val newList = tmpList.toList()
        updateInternalList(newList)
        isBeingUpdated = false
        onDone(newList)
    }

    public override fun getItem(position: Int): T {
        if (isBeingUpdated) {
            return tmpList[position]
        }
        return super.getItem(position)
    }

    // manually update the internal mList field to avoid running animations again
    private fun updateInternalList(newList: List<T>) {
        val differField = ListAdapter::class.java.getDeclaredField("mDiffer")
        differField.isAccessible = true
        val differ = differField.get(this) as AsyncListDiffer<T>
        differField.isAccessible = false
        val listField = AsyncListDiffer::class.java.getDeclaredField("mList")
        listField.isAccessible = true
        listField.set(differ, newList)
        listField.isAccessible = false
    }

}