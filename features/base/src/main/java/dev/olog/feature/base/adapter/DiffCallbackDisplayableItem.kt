package dev.olog.feature.base.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.base.DisplayableItem

object DiffCallbackDisplayableItem : DiffUtil.ItemCallback<DisplayableItem>() {
    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem == newItem
    }
}