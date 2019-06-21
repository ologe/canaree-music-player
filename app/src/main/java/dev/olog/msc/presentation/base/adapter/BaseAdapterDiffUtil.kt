package dev.olog.msc.presentation.base.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.olog.presentation.model.BaseModel
import dev.olog.shared.assertBackgroundThread

class BaseAdapterDiffUtil<Model : BaseModel>(
        private val oldList: List<Model>,
        private val newList: List<Model>,
        private val extendAreItemTheSame: ((Int, Int, Model, Model) -> Boolean)?

) : DiffUtil.Callback() {

    init {
        assertBackgroundThread()
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem : Model?= oldList[oldItemPosition]
        val newItem : Model?= newList[newItemPosition]
        return oldItem?.mediaId == newItem?.mediaId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem : Model? = oldList[oldItemPosition]
        val newItem : Model? = newList[newItemPosition]

        var areTheSame = oldItem == newItem
        if (extendAreItemTheSame != null && oldItem != null && newItem != null){
            areTheSame = areTheSame && extendAreItemTheSame
                    .invoke(oldItemPosition, newItemPosition, oldItem, newItem)
        }
        return areTheSame
    }

    override fun getOldListSize(): Int =  oldList.size

    override fun getNewListSize(): Int = newList.size

}