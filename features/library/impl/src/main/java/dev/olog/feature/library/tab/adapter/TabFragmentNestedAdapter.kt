package dev.olog.feature.library.tab.adapter

import android.view.View
import dev.olog.core.MediaId
import dev.olog.image.provider.BindingsAdapter
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.elevateAlbumOnTouch
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.platform.adapter.setOnLongClickListener
import dev.olog.ui.model.DiffCallbackDisplayableItem
import dev.olog.ui.model.DisplayableAlbum
import dev.olog.ui.model.DisplayableItem
import kotlinx.android.synthetic.main.item_tab_album_last_played.view.*

internal class TabFragmentNestedAdapter(
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
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        require(item is DisplayableAlbum)

        holder.itemView.apply {
            BindingsAdapter.loadAlbumImage(holder.imageView!!, item.mediaId)
            quickAction.setId(item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
        }
    }

}