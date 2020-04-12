package dev.olog.feature.about.about

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.about.model.AboutItem

internal object AboutItemDiff : DiffUtil.ItemCallback<AboutItem>() {

    override fun areItemsTheSame(oldItem: AboutItem, newItem: AboutItem): Boolean {
        return oldItem.itemType == newItem.itemType
    }

    override fun areContentsTheSame(oldItem: AboutItem, newItem: AboutItem): Boolean {
        return oldItem == newItem
    }
}