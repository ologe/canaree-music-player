package dev.olog.feature.base.adapter.media

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaUri
import dev.olog.feature.base.R
import dev.olog.feature.base.adapter.BaseAdapter
import dev.olog.feature.base.adapter.Submittable
import dev.olog.shared.exhaustive

open class MediaListItemAdapter(
    private val onItemClick: (MediaUri) -> Unit,
    private val onItemLongClick: (MediaUri, View) -> Unit,
    private val direction: ItemDirection = ItemDirection.Vertical,
) : BaseAdapter<MediaListItem, RecyclerView.ViewHolder>(MediaListItem.DiffUtil),
    Submittable<MediaListItem> {

    companion object {
        private val AUTHOR = R.layout.item_media_author
        private val COLLECTION = R.layout.item_media_collection
        private val SONG = R.layout.item_media_song
        private val EPISODE = R.layout.item_media_episode
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is MediaListItem.Author -> AUTHOR
            is MediaListItem.Collection -> COLLECTION
            is MediaListItem.Track -> {
                when (item.isPodcast) {
                    true -> EPISODE
                    false -> SONG
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh = when (viewType) {
            AUTHOR -> AuthorViewHolder(parent, direction)
            COLLECTION -> CollectionViewHolder(parent, direction)
            SONG -> SongViewHolder(parent)
            EPISODE -> EpisodeViewHolder(parent)
            else -> error("invalid type=$viewType")
        }
        vh.setupDefaultClickListeners(
            onClick = { item, _ -> onItemClick(item.uri) },
            onLongClick = { item, _ -> onItemLongClick(item.uri, vh.itemView) },
        )
        vh.setupElevateOnTouch()
        return vh
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: MediaListItem,
        position: Int
    ) {
        when {
            holder is CollectionViewHolder && item is MediaListItem.Collection -> holder.bind(item)
            holder is AuthorViewHolder && item is MediaListItem.Author -> holder.bind(item)
            holder is SongViewHolder && item is MediaListItem.Track -> holder.bind(item)
            holder is EpisodeViewHolder && item is MediaListItem.Track -> holder.bind(item)
            else -> error("invalid holder=${holder::class.java.name}")
        }.exhaustive
    }

}
