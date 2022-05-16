package dev.olog.platform.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.olog.shared.extension.swap
import java.util.*


abstract class DiffAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffCallback) {

    companion object {
        private val differUtils = DifferUtils()
    }

    override fun submitList(list: List<T>?) {
        super.submitList(list)
    }

    override fun submitList(list: List<T>?, commitCallback: Runnable?) {
        super.submitList(list, commitCallback)
    }

    override fun getItem(position: Int): T = super.getItem(position)

    fun swap(from: Int, to: Int) {
        val newList = currentList.toMutableList().apply {
            if (from < to) {
                for (i in from until to) {
                    swap(i, i + 1)
                }
            } else {
                for (i in from downTo to + 1) {
                    swap(i, i - 1)
                }
            }
        }
        differUtils.setList(this, newList)
        notifyItemMoved(to, from)
    }


    class DifferUtils {
        private val mDiffer = ListAdapter::class.java.getDeclaredField("mDiffer").apply {
            isAccessible = true
        }
        private val mList = AsyncListDiffer::class.java.getDeclaredField("mList").apply {
            isAccessible = true
        }
        private val mReadOnlyList = AsyncListDiffer::class.java.getDeclaredField("mReadOnlyList").apply {
            isAccessible = true
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> setList(adapter: ListAdapter<T, *>, list: List<T>) {
            val differ = mDiffer.get(adapter) as AsyncListDiffer<T>
            mList.set(differ, list)
            mReadOnlyList.set(differ, Collections.unmodifiableList(list))
        }

    }

}