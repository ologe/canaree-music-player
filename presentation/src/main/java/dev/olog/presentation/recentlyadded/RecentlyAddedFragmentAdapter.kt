package dev.olog.presentation.recentlyadded

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.media.MediaProvider
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.elevateAlbumOnTouch
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.platform.adapter.setOnDragListener
import dev.olog.platform.adapter.setOnLongClickListener
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.platform.adapter.drag.IDragListener
import dev.olog.platform.adapter.drag.TouchableAdapter
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.navigator.Navigator
import kotlinx.android.synthetic.main.item_recently_added.view.*

class RecentlyAddedFragmentAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val dragListener: IDragListener

) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem), TouchableAdapter {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
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

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        require(item is DisplayableTrack)

        holder.itemView.apply {
            BindingsAdapter.loadSongImage(holder.imageView!!, item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
            explicit.onItemChanged(item.title)
        }
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.item_recently_added
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        val item = getItem(viewHolder.adapterPosition)!!
        mediaProvider.addToPlayNext(item.mediaId)
    }

    override fun contentViewFor(holder: RecyclerView.ViewHolder): View {
        return holder.itemView.content
    }
}