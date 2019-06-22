package dev.olog.msc.presentation.playing.queue

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.presentation.base.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.presentation.navigator.Navigator
import dev.olog.msc.presentation.playing.queue.model.DisplayableQueueSong
import dev.olog.msc.utils.k.extension.*
import dev.olog.shared.textColorPrimary
import dev.olog.shared.textColorSecondary
import kotlinx.android.synthetic.main.item_playing_queue.view.*
import javax.inject.Inject

class PlayingQueueFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val mediaProvider: MediaProvider,
        private val navigator: Navigator

) : AbsAdapter<DisplayableQueueSong>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            mediaProvider.skipToQueueItem(item.mediaId.leaf!!)
        }

        viewHolder.setOnLongClickListener(controller) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.setOnMoveListener(controller, touchHelper)
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableQueueSong, position: Int) {
        binding.setVariable(BR.item, item)

        val view = binding.root
        val textColor = if (item.positionInList.length > 1 && item.positionInList.startsWith("-"))
            view.context.textColorSecondary() else view.context.textColorPrimary()
        binding.root.index.setTextColor(textColor)
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
        return viewType == R.layout.item_playing_queue
    }

    override val onDragAction = { from: Int, to: Int ->
        mediaProvider.swap(from, to)
    }

    override val onSwipeRightAction = { position: Int ->
        mediaProvider.remove(position)
    }

}