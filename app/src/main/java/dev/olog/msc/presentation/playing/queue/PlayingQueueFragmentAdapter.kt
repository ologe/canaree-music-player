package dev.olog.msc.presentation.playing.queue

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.databinding.ViewDataBinding
import dev.olog.msc.BR
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.adapter.BaseListAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import javax.inject.Inject

class PlayingQueueFragmentAdapter @Inject constructor(
        @ApplicationContext context: Context,
        @FragmentLifecycle lifecycle: Lifecycle,
        private val mediaProvider: MediaProvider,
        private val navigator: Navigator

) : BaseListAdapter<DisplayableItem>(lifecycle, context) {

    private var currentPosition : Int = -1

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        viewHolder.setOnClickListener(dataController) { item, _ ->
            mediaProvider.skipToQueueItem(item.trackNumber.toLong())
        }
        viewHolder.setOnLongClickListener(dataController) { item, _ ->
            navigator.toDialog(item, viewHolder.itemView)
        }
//        viewHolder.itemView.dragHandle.setOnTouchListener { _, event ->
//            if(event.actionMasked == MotionEvent.ACTION_DOWN) {
//                touchHelper()?.startDrag(viewHolder)
//                true
//            } else false
//        }
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

//    override val touchCallbackConfig: TouchCallbackConfig = TouchCallbackConfig(
//            true, true,
//            draggableViewType = R.layout.item_playing_queue,
//            onDragAction = { from, to -> musicController.swap(from, to)},
//            onSwipeAction = { position -> musicController.remove(position) }
//    )
}