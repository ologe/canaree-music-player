package dev.olog.feature.library.tab.adapter

import dev.olog.feature.presentation.base.adapter.*
import dev.olog.feature.presentation.base.loadAlbumImage
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.navigation.Navigator
import dev.olog.feature.presentation.base.model.toDomain

internal class TabFragmentNestedAdapter(
    private val navigator: Navigator
) : ObservableAdapter<DisplayableAlbum>(DiffCallbackDisplayableAlbum) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, view ->
            navigator.toDetailFragment(item.mediaId.toDomain(), view)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId.toDomain(), viewHolder.itemView, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableAlbum, position: Int) {

        holder.itemView.apply {
            transitionName = "tab nested ${item.mediaId}"
            holder.imageView!!.loadAlbumImage(item.mediaId.toDomain())
//            quickAction.setId(item.mediaId)
//            firstText.text = item.title
//            secondText.text = item.subtitle
        }
    }

}