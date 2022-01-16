package dev.olog.feature.base.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.base.model.DisplayableItem

@Deprecated("")
object DiffCallbackDisplayableItem : DiffUtil.ItemCallback<DisplayableItem>() {
    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.uri == newItem.uri
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem == newItem
    }
}