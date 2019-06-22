package dev.olog.presentation.base

import androidx.recyclerview.widget.DiffUtil
import dev.olog.presentation.model.BaseModel

class AdapterDiffUtil<Model : BaseModel>(
    private val oldList: List<Model>,
    private val newList: List<Model>

) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem: Model? = oldList[oldItemPosition]
        val newItem: Model? = newList[newItemPosition]
        return oldItem?.mediaId == newItem?.mediaId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem: Model? = oldList[oldItemPosition]
        val newItem: Model? = newList[newItemPosition]

        return oldItem == newItem
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

}