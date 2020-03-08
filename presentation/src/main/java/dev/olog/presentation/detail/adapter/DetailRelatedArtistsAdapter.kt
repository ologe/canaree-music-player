package dev.olog.presentation.detail.adapter

import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.loadAlbumImage
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator
import kotlinx.android.synthetic.main.item_detail_related_artist.view.*

class DetailRelatedArtistsAdapter(
    private val navigator: Navigator
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, view ->
            navigator.toDetailFragment(item.mediaId, view)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        require(item is DisplayableAlbum)

        holder.itemView.apply {
            transitionName = "related ${item.mediaId}"
            holder.imageView!!.loadAlbumImage(item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
            quickAction.setId(item.mediaId)
        }
    }
}