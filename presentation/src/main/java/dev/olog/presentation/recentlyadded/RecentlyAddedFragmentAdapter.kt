package dev.olog.presentation.recentlyadded

import androidx.recyclerview.widget.RecyclerView
import dev.olog.lib.media.MediaProvider
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.navigator.NavigatorLegacy
import kotlinx.android.synthetic.main.item_recently_added.*

class RecentlyAddedFragmentAdapter(
    private val navigator: NavigatorLegacy,
    private val mediaProvider: MediaProvider,
    private val dragListener: IDragListener
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem),
    TouchableAdapter {

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
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
        holder: LayoutContainerViewHolder,
        item: DisplayableItem,
        position: Int
    ) = holder.bindView {
        require(item is DisplayableTrack)

        ImageLoader.loadSongImage(imageView!!, item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
        explicit.onItemChanged(item.title)
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.item_recently_added
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        val item = getItem(viewHolder.adapterPosition)
        mediaProvider.addToPlayNext(item.mediaId)
    }

}