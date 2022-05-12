package dev.olog.feature.detail.main.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import dev.olog.core.MediaId
import dev.olog.image.provider.BindingsAdapter
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.elevateSongOnTouch
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.platform.adapter.setOnLongClickListener
import dev.olog.feature.detail.R
import dev.olog.ui.model.DisplayableTrack
import kotlinx.android.synthetic.main.item_detail_song_most_played.view.*

class DetailMostPlayedAdapter(
    private val onItemClick: (MediaId) -> Unit,
    private val onItemLongClick: (View, MediaId) -> Unit,
) : ObservableAdapter<DisplayableTrack>(DiffCallbackMostPlayed) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            onItemClick(item.mediaId)
        }

        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            onItemLongClick(viewHolder.itemView, item.mediaId)
        }

        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            onItemLongClick(view, item.mediaId)
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableTrack, position: Int) {
        holder.itemView.apply {
            BindingsAdapter.loadSongImage(holder.imageView!!, item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
            index.text = (item.idInPlaylist + 1).toString()
            explicit.onItemChanged(item.title)
        }
    }

    override fun onBindViewHolder(
        holder: DataBoundViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val positionInList = (payloads[0] as Int + 1).toString()
            holder.itemView.index.text = positionInList
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

}

internal object DiffCallbackMostPlayed : DiffUtil.ItemCallback<DisplayableTrack>() {
    override fun areItemsTheSame(oldItem: DisplayableTrack, newItem: DisplayableTrack): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: DisplayableTrack, newItem: DisplayableTrack): Boolean {
        val sameTitle = oldItem.title == newItem.title
        val sameArtist = oldItem.artist == newItem.artist
        val sameAlbum = oldItem.album == newItem.album
        val sameIndex = oldItem.idInPlaylist == newItem.idInPlaylist
        return sameTitle && sameArtist && sameAlbum && sameIndex
    }

    override fun getChangePayload(oldItem: DisplayableTrack, newItem: DisplayableTrack): Any? {
        if (oldItem.idInPlaylist != newItem.idInPlaylist) {
            return newItem.idInPlaylist
        }
        return super.getChangePayload(oldItem, newItem)
    }
}