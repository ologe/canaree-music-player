package dev.olog.presentation.tab.adapter

import androidx.lifecycle.Lifecycle
import dev.olog.presentation.BindingsAdapter
import dev.olog.feature.base.model.DisplayableAlbum
import dev.olog.feature.base.model.DisplayableItem
import dev.olog.feature.base.adapter.*
import dev.olog.presentation.navigator.Navigator
import kotlinx.android.synthetic.main.item_tab_album_last_played.view.*

class TabFragmentNestedAdapter(
    lifecycle: Lifecycle,
    private val navigator: Navigator

) : ObservableAdapter<DisplayableItem>(
    lifecycle,
    DiffCallbackDisplayableItem
) {

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