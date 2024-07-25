package dev.olog.presentation.recentlyadded

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.databinding.ItemRecentlyAddedBinding
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.compose.glide.BindingAdapters

class RecentlyAddedFragmentAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val dragListener: IDragListener

) : CustomListAdapter<RecentlyAddedItem, ItemRecentlyAddedBinding>(), TouchableAdapter {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): ItemRecentlyAddedBinding {
        return ItemRecentlyAddedBinding.inflate(inflater, parent, false)
    }

    override fun initViewHolderListeners(
        viewHolder: CustomViewHolder<ItemRecentlyAddedBinding>,
        viewType: Int
    ) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playFromMediaId(item.mediaId, null, null)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            navigator.toDialog(item.mediaId, view)
        }
        viewHolder.elevateAlbumOnTouch()
        viewHolder.setOnDragListener(R.id.dragHandle, dragListener)
    }

    override fun bind(
        holder: CustomViewHolder<ItemRecentlyAddedBinding>,
        item: RecentlyAddedItem,
        position: Int
    ) {
        holder.binding.apply {
            BindingAdapters.loadSongImage(cover, item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
            explicit.onItemChanged(item.title)
        }
    }

    override fun canInteractWithViewHolder(viewHolder: ViewHolder): Boolean {
        return viewHolder.itemViewType == R.layout.item_recently_added
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        val item = getItem(viewHolder.adapterPosition)
        mediaProvider.addToPlayNext(item.mediaId)
    }

}

data class RecentlyAddedItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)