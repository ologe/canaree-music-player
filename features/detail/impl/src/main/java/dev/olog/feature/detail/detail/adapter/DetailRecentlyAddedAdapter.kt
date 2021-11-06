package dev.olog.feature.detail.detail.adapter

import androidx.lifecycle.Lifecycle
import dev.olog.media.MediaProvider
import dev.olog.feature.base.BindingsAdapter
import dev.olog.feature.base.Navigator
import dev.olog.feature.base.model.DisplayableItem
import dev.olog.feature.base.model.DisplayableTrack
import dev.olog.feature.base.adapter.*
import dev.olog.feature.detail.R
import kotlinx.android.synthetic.main.item_detail_related_artist.view.firstText
import kotlinx.android.synthetic.main.item_detail_related_artist.view.secondText
import kotlinx.android.synthetic.main.item_detail_song_recent.view.*

class DetailRecentlyAddedAdapter(
    lifecycle: Lifecycle,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider

) : ObservableAdapter<DisplayableItem>(lifecycle,
    DiffCallbackDisplayableItem
) {

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