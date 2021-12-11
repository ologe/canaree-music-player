package dev.olog.feature.queue

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.feature.base.adapter.*
import dev.olog.media.MediaProvider
import dev.olog.feature.base.BindingsAdapter
import dev.olog.feature.base.drag.IDragListener
import dev.olog.feature.base.drag.TouchableAdapter
import dev.olog.feature.base.model.DisplayableQueueSong
import dev.olog.shared.android.extensions.textColorPrimary
import dev.olog.shared.android.extensions.textColorSecondary
import dev.olog.shared.swap
import kotlinx.android.synthetic.main.item_playing_queue.view.*

class PlayingQueueFragmentAdapter(
    lifecycle: Lifecycle,
    private val mediaProvider: MediaProvider,
    private val onItemLongClick: (MediaId, View) -> Unit,
    private val dragListener: IDragListener,
    private val viewModel: PlayingQueueFragmentViewModel
) : ObservableAdapter<DisplayableQueueSong>(
    lifecycle,
    DiffCallbackPlayingQueue
), TouchableAdapter {

    private val moves = mutableListOf<Pair<Int, Int>>()

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.skipToQueueItem(item.idInPlaylist)
        }

        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            onItemLongClick(item.mediaId, viewHolder.itemView)
        }
        viewHolder.setOnDragListener(R.id.dragHandle, dragListener)
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableQueueSong, position: Int) {
        holder.itemView.apply {
            BindingsAdapter.loadSongImage(holder.imageView!!, item.mediaId)
            index.text = item.relativePosition
            BindingsAdapter.setBoldIfTrue(firstText, item.isCurrentSong)
            firstText.text = item.title
            secondText.text = item.subtitle
            explicit.onItemChanged(item.title)

            val textColor = calculateTextColor(context, item.relativePosition)
            index.setTextColor(textColor)
        }
    }

    private fun calculateTextColor(context: Context, positionInList: String): Int {
        return if (positionInList.startsWith("-")) context.textColorSecondary()
        else context.textColorPrimary()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(
        holder: DataBoundViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val payload = payloads[0] as List<Any>
            for (currentPayload in payload) {
                when (currentPayload) {
                    is Boolean -> BindingsAdapter.setBoldIfTrue(holder.itemView.firstText, currentPayload)
                    is String -> {
                        val item = getItem(position)!!
                        val textColor = calculateTextColor(
                            holder.itemView.context,
                            item.relativePosition
                        )
                        holder.itemView.index.updateText(currentPayload, textColor)
                    }
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)

        }
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.item_playing_queue
    }

    override fun onMoved(from: Int, to: Int) {
        mediaProvider.swap(from, to)
        dataSet.swap(from, to)
        notifyItemMoved(from, to)
        moves.add(from to to)
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        mediaProvider.remove(viewHolder.adapterPosition)
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition
        dataSet.removeAt(position)
        notifyItemRemoved(position)
        viewModel.recalculatePositionsAfterRemove(position)
    }

    override fun onClearView() {
        viewModel.recalculatePositionsAfterMove(moves.toList())
        moves.clear()
    }

}

object DiffCallbackPlayingQueue : DiffUtil.ItemCallback<DisplayableQueueSong>() {
    override fun areItemsTheSame(
        oldItem: DisplayableQueueSong,
        newItem: DisplayableQueueSong
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: DisplayableQueueSong,
        newItem: DisplayableQueueSong
    ): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(
        oldItem: DisplayableQueueSong,
        newItem: DisplayableQueueSong
    ): Any? {
        val mutableList = mutableListOf<Any>()
        if (oldItem.relativePosition != newItem.relativePosition) {
            mutableList.add(newItem.relativePosition)
        }
        if (!oldItem.isCurrentSong && newItem.isCurrentSong) {
            mutableList.add(true)
        } else if (oldItem.isCurrentSong && !newItem.isCurrentSong) {
            mutableList.add(false)
        }
        if (mutableList.isNotEmpty()) {
            return mutableList
        }
        return super.getChangePayload(oldItem, newItem)
    }
}