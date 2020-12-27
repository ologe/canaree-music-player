package dev.olog.presentation.detail.adapter

import dev.olog.lib.media.MediaProvider
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.navigator.NavigatorLegacy
import kotlinx.android.synthetic.main.item_detail_song_recent.*

class DetailRecentlyAddedAdapter(
    private val navigator: NavigatorLegacy,
    private val mediaProvider: MediaProvider
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playFromMediaId(item.mediaId, null, null)
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
        item: DisplayableItem,
        position: Int
    ) = holder.bindView {
        require(item is DisplayableTrack)

        BindingsAdapter.loadSongImage(imageView!!, item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
        explicit.onItemChanged(item.title)
    }

}