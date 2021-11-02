package dev.olog.feature.base.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.base.BaseModel

class AdapterDiffUtil<Model : BaseModel>(
    private val oldList: List<Model>,
    private val newList: List<Model>,
    private val itemCallback: DiffUtil.ItemCallback<Model>

) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem: Model = oldList[oldItemPosition]
        val newItem: Model = newList[newItemPosition]
        return itemCallback.areItemsTheSame(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem: Model = oldList[oldItemPosition]
        val newItem: Model = newList[newItemPosition]

        return itemCallback.areContentsTheSame(oldItem, newItem)
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem: Model = oldList[oldItemPosition]
        val newItem: Model = newList[newItemPosition]
        return itemCallback.getChangePayload(oldItem, newItem)
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

}