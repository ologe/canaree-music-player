package dev.olog.feature.library.model

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.library.model.DisplayableFile

object DiffCallbackDisplayableFile : DiffUtil.ItemCallback<DisplayableFile>() {
    override fun areItemsTheSame(oldItem: DisplayableFile, newItem: DisplayableFile): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: DisplayableFile, newItem: DisplayableFile): Boolean {
        return oldItem == newItem
    }
}