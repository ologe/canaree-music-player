package dev.olog.feature.queue

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.feature.media.api.MediaProvider
import dev.olog.image.provider.BindingsAdapter
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.drag.IDragListener
import dev.olog.platform.adapter.drag.TouchableAdapter
import dev.olog.platform.adapter.elevateSongOnTouch
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.platform.adapter.setOnDragListener
import dev.olog.platform.adapter.setOnLongClickListener
import dev.olog.ui.textColorPrimary
import dev.olog.ui.textColorSecondary
import kotlinx.android.synthetic.main.item_playing_queue.view.*

class PlayingQueueFragmentAdapter(
    private val mediaProvider: MediaProvider,
    private val dragListener: IDragListener,
    private val viewModel: PlayingQueueFragmentViewModel,
    private val onItemLongClick: (View, MediaId) -> Unit,
) : ObservableAdapter<DisplayableQueueSong>(DiffCallbackPlayingQueue), TouchableAdapter {

    private val moves = mutableListOf<Pair<Int, Int>>()

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.skipToQueueItem(item.idInPlaylist)
        }

        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            onItemLongClick(viewHolder.itemView, item.mediaId)
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
        swap(from, to)
        moves.add(from to to)
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        mediaProvider.remove(viewHolder.adapterPosition)
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition
        removeAt(position)
        viewModel.recalculatePositionsAfterRemove(position)
    }

    override fun onClearView() {
        viewModel.recalculatePositionsAfterMove(moves.toList())
        moves.clear()
    }

    override fun contentViewFor(holder: RecyclerView.ViewHolder): View {
        return holder.itemView.content
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