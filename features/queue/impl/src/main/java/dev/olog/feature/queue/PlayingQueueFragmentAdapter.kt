package dev.olog.feature.queue

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.platform.adapter.DiffAdapter
import dev.olog.platform.adapter.drag.IDragListener
import dev.olog.platform.adapter.drag.TouchableAdapter
import dev.olog.platform.adapter.elevateSongOnTouch
import dev.olog.platform.adapter.setOnDragListener

class PlayingQueueFragmentAdapter(
    private val dragListener: IDragListener,
    private val onItemClick: (QueueItem) -> Unit,
    private val onItemLongClick: (View, MediaId) -> Unit,
    private val onItemMoved: (from: Int, to: Int) -> Unit,
    private val onItemClear: () -> Unit,
    private val onSwipeRight: (Int) -> Unit,
    private val afterSwipeRight: (Int) -> Unit,
) : DiffAdapter<QueueItem, PlayingQueueViewHolder>(Diff),
    TouchableAdapter {

    companion object Diff : DiffUtil.ItemCallback<QueueItem>() {
        override fun areItemsTheSame(oldItem: QueueItem, newItem: QueueItem): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: QueueItem, newItem: QueueItem): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: QueueItem, newItem: QueueItem): Any {
            return newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayingQueueViewHolder {
        val vh = PlayingQueueViewHolder(parent)

        vh.itemView.setOnClickListener {
            val item = getItem(vh.bindingAdapterPosition)
            onItemClick(item)
        }
        vh.itemView.setOnLongClickListener {
            val item = getItem(vh.bindingAdapterPosition)
            onItemLongClick(it, item.mediaId)
            true
        }
        vh.elevateSongOnTouch()
        vh.setOnDragListener(R.id.dragHandle, dragListener)

        return vh
    }

    override fun onBindViewHolder(holder: PlayingQueueViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: PlayingQueueViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }

        val item = payloads.first() as QueueItem
        holder.rebind(item)
    }

    override fun canInteractWithViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder is PlayingQueueViewHolder
    }

    override fun onMoved(from: Int, to: Int) {
        onItemMoved(from, to)
        swap(from, to)
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        onSwipeRight(viewHolder.bindingAdapterPosition)
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {
        afterSwipeRight(viewHolder.bindingAdapterPosition)
    }

    override fun onClearView() {
        onItemClear()
    }

    override fun contentViewFor(holder: RecyclerView.ViewHolder): View {
        return holder.itemView.findViewById(R.id.content)
    }
}