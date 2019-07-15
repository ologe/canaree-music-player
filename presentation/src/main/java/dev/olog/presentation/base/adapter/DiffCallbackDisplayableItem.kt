package dev.olog.presentation.base.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import dev.olog.presentation.model.DisplayableItem

object DiffCallbackDisplayableItem : DiffUtil.ItemCallback<DisplayableItem>() {
    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem == newItem
    }
}