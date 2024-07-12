package dev.olog.presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import dev.olog.core.MediaId
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.base.adapter.CustomListAdapter
import dev.olog.presentation.base.adapter.CustomViewHolder
import dev.olog.presentation.base.adapter.elevateAlbumOnTouch
import dev.olog.presentation.base.adapter.setOnClickListener
import dev.olog.presentation.base.adapter.setOnLongClickListener
import dev.olog.presentation.databinding.ItemDetailRelatedArtistBinding
import dev.olog.presentation.navigator.Navigator

class DetailRelatedArtistsAdapter(
    private val navigator: Navigator
) : CustomListAdapter<DetailRelatedArtistsItem, ItemDetailRelatedArtistBinding>() {

    override fun inflate(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemDetailRelatedArtistBinding {
        return ItemDetailRelatedArtistBinding.inflate(inflater, parent, false)
    }

    override fun initViewHolderListeners(
        viewHolder: CustomViewHolder<ItemDetailRelatedArtistBinding>,
        viewType: Int
    ) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            navigator.toDetailFragment(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(
        holder: CustomViewHolder<ItemDetailRelatedArtistBinding>,
        item: DetailRelatedArtistsItem,
        position: Int
    ) {
        holder.binding.apply {
            BindingsAdapter.loadAlbumImage(cover, item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
            quickAction.setId(item.mediaId)
        }
    }
}

data class DetailRelatedArtistsItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)