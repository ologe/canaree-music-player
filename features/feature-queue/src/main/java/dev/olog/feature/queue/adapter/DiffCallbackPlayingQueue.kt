package dev.olog.feature.queue.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.queue.model.DisplayableQueueSong

internal object DiffCallbackPlayingQueue : DiffUtil.ItemCallback<DisplayableQueueSong>() {
    override fun areItemsTheSame(
        oldItem: DisplayableQueueSong,
        newItem: DisplayableQueueSong
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: DisplayableQueueSong,
        newItem: DisplayableQueueSong
    ): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(
        oldItem: DisplayableQueueSong,
        newItem: DisplayableQueueSong
    ): Any? {
        if (oldItem.relativePosition != newItem.relativePosition) {
            return newItem.relativePosition
        }
        return super.getChangePayload(oldItem, newItem)
    }
}