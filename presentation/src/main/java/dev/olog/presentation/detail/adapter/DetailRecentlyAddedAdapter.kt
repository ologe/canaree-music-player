package dev.olog.presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.databinding.ItemDetailSongRecentBinding
import dev.olog.presentation.navigator.Navigator

class DetailRecentlyAddedAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider
) : CustomListAdapter<DetailRecentlyAddedItem, ItemDetailSongRecentBinding>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): ItemDetailSongRecentBinding {
        return ItemDetailSongRecentBinding.inflate(inflater, parent, false)
    }

    override fun initViewHolderListeners(
        viewHolder: CustomViewHolder<ItemDetailSongRecentBinding>,
        viewType: Int
    ) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playRecentlyAdded(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }

        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            navigator.toDialog(item.mediaId, view)
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(
        holder: CustomViewHolder<ItemDetailSongRecentBinding>,
        item: DetailRecentlyAddedItem,
        position: Int
    ) {
        holder.binding.apply {
            BindingsAdapter.loadSongImage(cover, item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
            explicit.onItemChanged(item.title)
        }
    }

}

data class DetailRecentlyAddedItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)