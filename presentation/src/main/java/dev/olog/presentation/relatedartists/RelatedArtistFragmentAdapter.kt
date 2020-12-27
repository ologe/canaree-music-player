package dev.olog.presentation.relatedartists

import dev.olog.feature.base.adapter.*
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.NavigatorLegacy
import kotlinx.android.synthetic.main.item_related_artist.*

class RelatedArtistFragmentAdapter(
    private val navigator: NavigatorLegacy
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {


    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            navigator.toDetailFragment(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: DisplayableItem,
        position: Int
    ) = holder.bindView {
        require(item is DisplayableAlbum)

        ImageLoader.loadAlbumImage(imageView!!, item.mediaId)
        quickAction.setId(item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
    }


}