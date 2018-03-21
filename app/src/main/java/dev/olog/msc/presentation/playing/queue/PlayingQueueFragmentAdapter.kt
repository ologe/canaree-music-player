package dev.olog.msc.presentation.playing.queue

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import kotlinx.android.synthetic.main.item_playing_queue.view.*
import javax.inject.Inject

class PlayingQueueFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val mediaProvider: MediaProvider,
        private val navigator: Navigator

) : AbsAdapter<DisplayableItem>(lifecycle) {

    var currentPosition : Int = -1

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            mediaProvider.skipToQueueItem(item.trackNumber.toLong())
        }

        viewHolder.setOnLongClickListener(controller) { item, _, _ ->
            navigator.toDialog(item, viewHolder.itemView)
        }
        viewHolder.itemView.dragHandle.setOnTouchListener { _, event ->
            when (event.actionMasked){
                MotionEvent.ACTION_DOWN -> {
                    touchHelper?.startDrag(viewHolder)
                    true
                }
                else -> false
            }
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.isCurrentSong, currentPosition == position)
        when {
            currentPosition == -1 -> binding.setVariable(BR.index, "-")
            position > currentPosition -> binding.setVariable(BR.index, "+${position - currentPosition}")
            position < currentPosition -> binding.setVariable(BR.index, "${position - currentPosition}")
            else -> binding.setVariable(BR.index, "-")
        }
        val textColor = if (position < currentPosition) R.color.text_color_secondary else R.color.text_color_primary
        binding.root.index.setTextColor(ContextCompat.getColor(binding.root.context, textColor))
    }

    override fun onMoved(from: Int, to: Int) {
        if (currentPosition == from){
            currentPosition = to
        }

        super.onMoved(from, to)
    }

    override fun onSwiped(position: Int) {
        if (currentPosition == position){
            currentPosition = -1
        }
        if (currentPosition > position){
            currentPosition--
        }
        super.onSwiped(position)
    }

    override fun onInteractionEnd(position: Int) {
        notifyItemRangeChanged(0, controller.getSize())
    }

    fun updateCurrentPosition(idInPlaylist: Int) {
        currentPosition = indexOf { it.trackNumber.toInt() == idInPlaylist }
        notifyDataSetChanged()
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
        return viewType == R.layout.item_playing_queue
    }

    override val onDragAction = { from: Int, to: Int -> mediaProvider.swap(from, to) }

    override val onSwipeAction = { position: Int ->
        mediaProvider.remove(position)
    }

}