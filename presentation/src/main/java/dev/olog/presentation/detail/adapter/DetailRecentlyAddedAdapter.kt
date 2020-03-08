package dev.olog.presentation.detail.adapter

import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.loadSongImage
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.navigator.Navigator
import kotlinx.android.synthetic.main.item_detail_related_artist.view.firstText
import kotlinx.android.synthetic.main.item_detail_related_artist.view.secondText
import kotlinx.android.synthetic.main.item_detail_song_recent.view.explicit
import kotlinx.android.synthetic.main.item_detail_song_recent.view.isPlaying

class DetailRecentlyAddedAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem),
    CanShowIsPlaying by CanShowIsPlayingImpl(){

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playRecentlyAdded(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView, viewHolder.itemView)
        }

        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            navigator.toDialog(item.mediaId, view, viewHolder.itemView)
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun onBindViewHolder(
        holder: DataBoundViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val payload = payloads.filterIsInstance<Boolean>().firstOrNull()
        if (payload != null) {
            holder.itemView.isPlaying.toggleVisibility(payload)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        require(item is DisplayableTrack)

        holder.itemView.apply {
            transitionName = "recent ${item.mediaId}"
            holder.imageView!!.loadSongImage(item.mediaId)
            isPlaying.toggleVisibility(item.mediaId == playingMediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
            explicit.onItemChanged(item.title)
        }
    }

}