package dev.olog.presentation.detail.adapter

import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.loadAlbumImage
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.navigator.Navigator
import dev.olog.feature.presentation.base.model.toDomain
import kotlinx.android.synthetic.main.item_detail_related_artist.view.*

internal class DetailRelatedArtistsAdapter(
    private val navigator: Navigator
) : ObservableAdapter<DisplayableAlbum>(DiffCallbackDisplayableAlbum) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, view ->
            navigator.toDetailFragment(item.mediaId, view)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableAlbum, position: Int) {
        holder.itemView.apply {
            transitionName = "detail related ${item.mediaId}"
            holder.imageView!!.loadAlbumImage(item.mediaId.toDomain())
            firstText.text = item.title
            secondText.text = item.subtitle
            quickAction.setId(item.mediaId)
        }
    }
}