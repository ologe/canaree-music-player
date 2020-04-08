package dev.olog.feature.presentation.base.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.feature.presentation.base.model.DisplayableTrack

object DiffCallbackDisplayableItem : DiffUtil.ItemCallback<DisplayableItem>() {
    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem == newItem
    }
}

object DiffCallbackDisplayableAlbum : DiffUtil.ItemCallback<DisplayableAlbum>() {
    override fun areItemsTheSame(oldItem: DisplayableAlbum, newItem: DisplayableAlbum): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: DisplayableAlbum, newItem: DisplayableAlbum): Boolean {
        return oldItem == newItem
    }
}

object DiffCallbackDisplayableTrack : DiffUtil.ItemCallback<DisplayableTrack>() {

    override fun areItemsTheSame(oldItem: DisplayableTrack, newItem: DisplayableTrack): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: DisplayableTrack, newItem: DisplayableTrack): Boolean {
        return oldItem == newItem
    }
}