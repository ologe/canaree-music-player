package dev.olog.presentation.queue

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.olog.media.MediaProvider
import dev.olog.presentation.BR
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.model.DisplayableQueueSong
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.extensions.swap
import dev.olog.shared.extensions.textColorPrimary
import dev.olog.shared.extensions.textColorSecondary
import kotlinx.android.synthetic.main.item_playing_queue.view.*

class PlayingQueueFragmentAdapter(
    lifecycle: Lifecycle,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val dragListener: IDragListener

) : ObservableAdapter<DisplayableQueueSong>(
    lifecycle,
    DiffCallbackPlayingQueue
), TouchableAdapter {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.skipToQueueItem(item.idInPlaylist)
        }

        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.setOnDragListener(R.id.dragHandle, dragListener)
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableQueueSong, position: Int) {
        binding.setVariable(BR.item, item)

        val view = binding.root
        val textColor = calculateTextColor(view.context, item.relativePosition)
        binding.root.index.setTextColor(textColor)
    }

    private fun calculateTextColor(context: Context, positionInList: String): Int {
        return if (positionInList.startsWith("-")) context.textColorSecondary()
        else context.textColorPrimary()
    }

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
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        mediaProvider.remove(viewHolder.adapterPosition)
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {
        dataSet.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
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