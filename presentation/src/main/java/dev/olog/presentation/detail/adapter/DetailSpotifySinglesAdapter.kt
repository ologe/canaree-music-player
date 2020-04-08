package dev.olog.presentation.detail.adapter

import dev.olog.feature.presentation.base.adapter.*
import dev.olog.feature.presentation.base.loadAlbumImage
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.toDomain
import kotlinx.android.synthetic.main.item_detail_album_spotify.view.*

internal class DetailSpotifySinglesAdapter(

) : ObservableAdapter<DisplayableAlbum>(DiffCallbackDisplayableAlbum) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, view ->
            // TODO
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            // TODO
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableAlbum, position: Int) {
        holder.itemView.apply {
            transitionName = "detail spotify singles ${item.mediaId}"
            holder.imageView!!.loadAlbumImage(item.mediaId.toDomain())
            firstText.text = item.title
            secondText.text = item.subtitle
        }
    }
}