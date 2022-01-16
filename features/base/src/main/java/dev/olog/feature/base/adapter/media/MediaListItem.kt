package dev.olog.feature.base.adapter.media

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import dev.olog.core.MediaUri
import dev.olog.shared.TextUtils
import java.util.concurrent.TimeUnit

sealed class MediaListItem {

    companion object {
        val DiffUtil = MediaListItemCallback<MediaListItem>()
    }

    abstract val uri: MediaUri

    data class Track(
        override val uri: MediaUri,
        val title: String,
        val author: String,
        val collection: String,
        val duration: Long,
    ) : MediaListItem() {

        val isPodcast = uri.isPodcast
        val subtitle: String = "$author${TextUtils.MIDDLE_DOT_SPACED}$collection"
        val readableDuration: String = "${TimeUnit.MILLISECONDS.toMinutes(duration)}m"

    }

    data class Collection(
        override val uri: MediaUri,
        val title: String,
        val subtitle: String,
    ) : MediaListItem()

    data class Author(
        override val uri: MediaUri,
        val title: String,
        val subtitle: String,
    ) : MediaListItem()

}

class MediaListItemCallback<T : MediaListItem> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.uri == newItem.uri
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: T, newItem: T): Any = newItem
}