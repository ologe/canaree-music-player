package dev.olog.presentation.detail.adapter

import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.loadAlbumImage
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.toDomain
import kotlinx.android.synthetic.main.item_detail_album_spotify.view.*

internal class DetailSpotifyAlbumsAdapter(

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
            transitionName = "detail spotify albums ${item.mediaId}"
            holder.imageView!!.loadAlbumImage(item.mediaId.toDomain())
            firstText.text = item.title
            secondText.text = item.subtitle
        }
    }
}