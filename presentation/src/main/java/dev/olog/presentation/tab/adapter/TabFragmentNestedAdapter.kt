package dev.olog.presentation.tab.adapter

import dev.olog.feature.presentation.base.adapter.*
import dev.olog.feature.presentation.base.loadAlbumImage
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.presentation.navigator.Navigator
import dev.olog.feature.presentation.base.model.toDomain
import kotlinx.android.synthetic.main.item_tab_album_last_played.view.*

internal class TabFragmentNestedAdapter(
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
            transitionName = "tab nested ${item.mediaId}"
            holder.imageView!!.loadAlbumImage(item.mediaId.toDomain())
            quickAction.setId(item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
        }
    }

}