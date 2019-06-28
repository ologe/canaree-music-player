package dev.olog.presentation.queue

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import dev.olog.media.MediaProvider
import dev.olog.presentation.BR
import dev.olog.presentation.base.*
import dev.olog.presentation.model.DisplayableQueueSong
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.extensions.textColorPrimary
import dev.olog.shared.extensions.textColorSecondary
import kotlinx.android.synthetic.main.item_playing_queue.view.*

class PlayingQueueFragmentAdapter(
    lifecycle: Lifecycle,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator

) : ObservableAdapter<DisplayableQueueSong>(lifecycle, DiffCallbackDisplayableSong) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.skipToQueueItem(item.mediaId.leaf!!)
        }

        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
//        viewHolder.setOnMoveListener(controller, touchHelper) TODO
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableQueueSong, position: Int) {
        binding.setVariable(BR.item, item)

        val view = binding.root
        val textColor = if (item.positionInList.length > 1 && item.positionInList.startsWith("-"))
            view.context.textColorSecondary() else view.context.textColorPrimary()
        binding.root.index.setTextColor(textColor)
    }

//    override fun canInteractWithViewHolder(viewType: Int): Boolean? { TODO
//        return viewType == R.layout.item_playing_queue
//    }
//
//    override val onDragAction = { from: Int, to: Int ->
//        mediaProvider.swap(from, to)
//    }
//
//    override val onSwipeRightAction = { position: Int ->
//        mediaProvider.remove(position)
//    }

}

object DiffCallbackDisplayableSong : DiffUtil.ItemCallback<DisplayableQueueSong>() {
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
        return oldItem == newItem // TODO improve,
    }
}