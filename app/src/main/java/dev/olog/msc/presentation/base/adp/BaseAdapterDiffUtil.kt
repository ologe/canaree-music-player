package dev.olog.msc.presentation.base.adp

import android.support.v7.util.DiffUtil
import dev.olog.msc.presentation.base.BaseModel
import dev.olog.msc.utils.assertBackgroundThread

class BaseAdapterDiffUtil<Model : BaseModel>(
        private val oldList: List<Model>,
        private val newList: List<Model>,
        private val extendAreItemTheSame: ((Int, Int, Model, Model) -> Boolean)?

) : DiffUtil.Callback() {

    init {
        assertBackgroundThread()
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        var areTheSame = oldItem.mediaId == newItem.mediaId
        if (extendAreItemTheSame != null){
            areTheSame = areTheSame && extendAreItemTheSame
                    .invoke(oldItemPosition, newItemPosition, oldItem, newItem)
        }
        return areTheSame
    }

    override fun getOldListSize(): Int =  oldList.size

    override fun getNewListSize(): Int = newList.size

}