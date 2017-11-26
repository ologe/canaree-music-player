package dev.olog.presentation

import android.support.v7.util.DiffUtil
import dev.olog.presentation.fragment_detail.DetailDataType
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.assertBackgroundThread

class DetailDiff(
        private val oldData: Map<DetailDataType, List<DisplayableItem>>,
        private val newData: Map<DetailDataType, List<DisplayableItem>>

) : DiffUtil.Callback() {

    init {
        assertBackgroundThread()
    }

    override fun getOldListSize(): Int = oldData.values.sumBy { it.size }
    override fun getNewListSize(): Int = newData.values.sumBy { it.size }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = getItem(oldData, oldItemPosition)
        val newItem = getItem(newData, newItemPosition)
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = getItem(oldData, oldItemPosition)
        val newItem = getItem(newData, newItemPosition)
        return oldItem == newItem && oldItemPosition == newItemPosition // to update placeholders
    }

    private fun getItem(dataSet: Map<DetailDataType, List<DisplayableItem>>, position: Int): DisplayableItem {
        var totalSize = 0
        for (value in dataSet.values) {
            if (position in totalSize until (totalSize + value.size)){
                val realPosition = position - totalSize
                return value[realPosition]
            } else{
                totalSize += value.size
            }
        }
        throw IllegalArgumentException("invalid position $position")
    }

}
