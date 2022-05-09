package dev.olog.presentation.tab.adapter

import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.elevateAlbumOnTouch
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.platform.adapter.setOnLongClickListener
import dev.olog.presentation.BindingsAdapter
import dev.olog.ui.model.DisplayableAlbum
import dev.olog.ui.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator
import dev.olog.ui.model.DiffCallbackDisplayableItem
import kotlinx.android.synthetic.main.item_tab_album_last_played.view.*

internal class TabFragmentNestedAdapter(
    private val navigator: Navigator
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            navigator.toDetailFragment(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
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