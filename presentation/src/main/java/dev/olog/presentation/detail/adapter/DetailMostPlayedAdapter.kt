package dev.olog.presentation.detail.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.olog.media.MediaProvider
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.navigator.Navigator
import kotlinx.android.synthetic.main.item_detail_song_most_played.*

class DetailMostPlayedAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider
) : ObservableAdapter<DisplayableTrack>(DiffCallbackMostPlayed) {

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
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

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: DisplayableTrack,
        position: Int
    ) = holder.bindView {
        BindingsAdapter.loadSongImage(imageView!!, item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
        index.text = (item.idInPlaylist + 1).toString()
        explicit.onItemChanged(item.title)
    }

    override fun onBindViewHolder(
        holder: LayoutContainerViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) = holder.bindView {
        if (payloads.isNotEmpty()) {
            val positionInList = (payloads[0] as Int + 1).toString()
            index.text = positionInList
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