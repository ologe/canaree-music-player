package dev.olog.presentation.fragment_playing_queue

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.databinding.ViewDataBinding
import android.view.MotionEvent
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation._base.list.BaseListAdapter
import dev.olog.presentation._base.list.DataBoundViewHolder
import dev.olog.presentation._base.list.TouchCallbackConfig
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_music.MusicController
import dev.olog.presentation.utils.extension.elevateSongOnTouch
import dev.olog.presentation.utils.extension.setOnClickListener
import dev.olog.presentation.utils.extension.setOnLongClickListener
import dev.olog.shared.ApplicationContext
import kotlinx.android.synthetic.main.item_playing_queue.view.*
import javax.inject.Inject

class PlayingQueueFragmentAdapter @Inject constructor(
        @ApplicationContext context: Context,
        @FragmentLifecycle lifecycle: Lifecycle,
        private val musicController: MusicController,
        private val navigator: Navigator

) : BaseListAdapter<DisplayableItem>(lifecycle, context) {

    private var currentPosition : Int = -1

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        viewHolder.setOnClickListener(dataController) { item, _ ->
            musicController.skipToQueueItemWithIdInPlaylist(item.mediaId, item.trackNumber.toInt())
        }
        viewHolder.setOnLongClickListener(dataController) { item, _ ->
            navigator.toDialog(item, viewHolder.itemView)
        }
        viewHolder.itemView.dragHandle.setOnTouchListener { _, event ->
            if(event.actionMasked == MotionEvent.ACTION_DOWN) {
                touchHelper()?.startDrag(viewHolder)
                true
            } else false
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.isCurrentSong, item.trackNumber.toInt() == PlayingQueueFragmentViewModel.idInPlaylist)
        when {
            position > currentPosition -> binding.setVariable(BR.index, "+${position - currentPosition}")
            position < currentPosition -> binding.setVariable(BR.index, "${position - currentPosition}")
            else -> binding.setVariable(BR.index, "-")
        }
    }

    fun updateCurrentPosition(trackNumber: Int) {
        currentPosition = dataController.getItemPositionByPredicate { it.trackNumber.toInt() == trackNumber }
    }

    override val touchCallbackConfig: TouchCallbackConfig = TouchCallbackConfig(
            true, true,
            draggableViewType = R.layout.item_playing_queue,
            onDragAction = { from, to -> musicController.swap(from, to)},
            onSwipeAction = { position -> musicController.remove(position) }
    )
}