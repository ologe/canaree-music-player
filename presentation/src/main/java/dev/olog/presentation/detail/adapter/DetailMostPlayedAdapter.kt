package dev.olog.presentation.detail.adapter

import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import dev.olog.media.MediaProvider
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.feature.base.DisplayableTrack
import dev.olog.feature.base.adapter.*
import dev.olog.presentation.navigator.Navigator
import kotlinx.android.synthetic.main.item_detail_song_most_played.view.*

class DetailMostPlayedAdapter(
    lifecycle: Lifecycle,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider

) : ObservableAdapter<DisplayableTrack>(
    lifecycle,
    DiffCallbackMostPlayed
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playMostPlayed(item.mediaId)
        }

        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }

        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            navigator.toDialog(item.mediaId, view)
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