package dev.olog.presentation.detail.adapter

import dev.olog.feature.media.MediaProvider
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.elevateSongOnTouch
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.platform.adapter.setOnLongClickListener
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.ui.model.DisplayableItem
import dev.olog.ui.model.DisplayableTrack
import dev.olog.presentation.navigator.Navigator
import dev.olog.ui.model.DiffCallbackDisplayableItem
import kotlinx.android.synthetic.main.item_detail_related_artist.view.firstText
import kotlinx.android.synthetic.main.item_detail_related_artist.view.secondText
import kotlinx.android.synthetic.main.item_detail_song_recent.view.*

class DetailRecentlyAddedAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider

) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playRecentlyAdded(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }

        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            navigator.toDialog(item.mediaId, view)
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