package dev.olog.ui.model

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

object DiffCallbackDisplayableItem : DiffUtil.ItemCallback<DisplayableItem>() {
    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem == newItem
    }
}