package dev.olog.presentation.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.databinding.ItemDetailSongMostPlayedBinding
import dev.olog.presentation.navigator.Navigator

class DetailMostPlayedAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider
) : CustomListAdapter<DetailMostPlayedItem, ItemDetailSongMostPlayedBinding>(DiffCallbackMostPlayed) {

    override fun inflate(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemDetailSongMostPlayedBinding {
        return ItemDetailSongMostPlayedBinding.inflate(inflater, parent, false)
    }

    override fun initViewHolderListeners(
        viewHolder: CustomViewHolder<ItemDetailSongMostPlayedBinding>,
        viewType: Int
    ) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playMostPlayed(item.mediaId)
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
        holder: CustomViewHolder<ItemDetailSongMostPlayedBinding>,
        item: DetailMostPlayedItem,
        position: Int
    ) {
        holder.binding.apply {
            BindingsAdapter.loadSongImage(cover, item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
            index.text = item.position.toString()
            explicit.onItemChanged(item.title)
        }
    }

    override fun onBindViewHolder(
        holder: CustomViewHolder<ItemDetailSongMostPlayedBinding>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val positionInList = (payloads[0] as Int + 1).toString()
            holder.binding.index.text = positionInList
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

}

data class DetailMostPlayedItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
    val position: Int,
)

private object DiffCallbackMostPlayed : DiffUtil.ItemCallback<DetailMostPlayedItem>() {
    override fun areItemsTheSame(oldItem: DetailMostPlayedItem, newItem: DetailMostPlayedItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: DetailMostPlayedItem, newItem: DetailMostPlayedItem): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: DetailMostPlayedItem, newItem: DetailMostPlayedItem): Any? {
        if (oldItem.position != newItem.position) {
            return newItem.position
        }
        return super.getChangePayload(oldItem, newItem)
    }
}