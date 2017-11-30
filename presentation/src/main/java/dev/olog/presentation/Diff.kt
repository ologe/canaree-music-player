//package dev.olog.presentation
//
//import android.support.v7.util.DiffUtil
//import dev.olog.presentation.model.DisplayableItem
//import dev.olog.presentation.utils.assertBackgroundThread
//
//class Diff(
//        private val oldList: List<DisplayableItem>,
//        private val newList: List<DisplayableItem>
//
//) : DiffUtil.Callback() {
//
//    init {
//        assertBackgroundThread()
//    }
//
//    override fun getOldListSize(): Int = oldList.size
//
//    override fun getNewListSize(): Int = newList.size
//
//    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//        val oldItem = oldList[oldItemPosition]
//        val newItem = newList[newItemPosition]
//        return oldItem.mediaId == newItem.mediaId
//    }
//
//    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//        val oldItem = oldList[oldItemPosition]
//        val newItem = newList[newItemPosition]
//        return oldItem == newItem && oldItemPosition == newItemPosition // to update placeholders
//    }
//
//
//}
