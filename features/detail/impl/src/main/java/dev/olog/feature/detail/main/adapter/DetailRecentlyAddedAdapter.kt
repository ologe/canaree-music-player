package dev.olog.feature.detail.main.adapter

import android.view.View
import dev.olog.core.MediaId
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.elevateSongOnTouch
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.platform.adapter.setOnLongClickListener
import dev.olog.image.provider.BindingsAdapter
import dev.olog.feature.detail.R
import dev.olog.ui.model.DisplayableItem
import dev.olog.ui.model.DisplayableTrack
import dev.olog.ui.model.DiffCallbackDisplayableItem
import kotlinx.android.synthetic.main.item_detail_related_artist.view.firstText
import kotlinx.android.synthetic.main.item_detail_related_artist.view.secondText
import kotlinx.android.synthetic.main.item_detail_song_recent.view.*

class DetailRecentlyAddedAdapter(
    private val onItemClick: (MediaId) -> Unit,
    private val onItemLongClick: (View, MediaId) -> Unit,
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

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

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        require(item is DisplayableTrack)

        holder.itemView.apply {
            BindingsAdapter.loadSongImage(holder.imageView!!, item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
            explicit.onItemChanged(item.title)
        }
    }

}