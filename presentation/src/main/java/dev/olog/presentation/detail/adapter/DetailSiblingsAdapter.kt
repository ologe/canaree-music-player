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
import dev.olog.presentation.databinding.ItemDetailAlbumBinding
import dev.olog.presentation.navigator.Navigator

class DetailSiblingsAdapter(
    private val navigator: Navigator
) : CustomListAdapter<DetailSiblingsItem, ItemDetailAlbumBinding>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): ItemDetailAlbumBinding {
        return ItemDetailAlbumBinding.inflate(inflater, parent, false)
    }

    override fun initViewHolderListeners(
        viewHolder: CustomViewHolder<ItemDetailAlbumBinding>,
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
        holder: CustomViewHolder<ItemDetailAlbumBinding>,
        item: DetailSiblingsItem,
        position: Int
    ) {
        holder.binding.apply {
            BindingsAdapter.loadAlbumImage(cover, item.mediaId)
            quickAction.setId(item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
        }
    }
}

data class DetailSiblingsItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)