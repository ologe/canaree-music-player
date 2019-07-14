package dev.olog.presentation.base.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import dev.olog.presentation.model.DisplayableItem2

object DisplayableItemDiffCallback2 : DiffUtil.ItemCallback<DisplayableItem2>() {
    override fun areItemsTheSame(oldItem: DisplayableItem2, newItem: DisplayableItem2): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DisplayableItem2, newItem: DisplayableItem2): Boolean {
        return oldItem == newItem
    }
}