package dev.olog.msc.presentation.player

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.databinding.ViewDataBinding
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.dagger.ApplicationContext
import dev.olog.msc.dagger.FragmentLifecycle
import dev.olog.msc.presentation.MusicController
import dev.olog.msc.presentation.base.adapter.BaseListAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import javax.inject.Inject

class MiniQueueFragmentAdapter @Inject constructor(
        @ApplicationContext context: Context,
        @FragmentLifecycle lifecycle: Lifecycle,
        private val musicController: MusicController,
        private val navigator: Navigator

): BaseListAdapter<DisplayableItem>(lifecycle, context) {

    var hasGranularUpdate: Boolean = false

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {

        when (viewType){
            R.layout.item_mini_queue -> {
                viewHolder.setOnClickListener(dataController) { item, _ ->
                    musicController.skipToQueueItemWithIdInPlaylist(item.mediaId, item.trackNumber.toInt())
                }
                viewHolder.setOnLongClickListener(dataController) { item, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, dataController) { item, _, view ->
                    navigator.toDialog(item, view)
                }

//                viewHolder.itemView.dragHandle.setOnTouchListener { _, event ->
//                    if(event.actionMasked == MotionEvent.ACTION_DOWN) {
//                        touchHelper()?.startDrag(viewHolder)
//                        true
//                    } else false
//                }
                viewHolder.elevateSongOnTouch()
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    override fun hasGranularUpdate(): Boolean = hasGranularUpdate

//    override val touchCallbackConfig = TouchCallbackConfig(
//            true, true,
//            draggableViewType = R.layout.item_mini_queue,
//            onDragAction = { from, to ->
//                musicController.swapRelative(from, to)
//            }, onSwipeAction = { position -> musicController.removeRelative(position) }
//    )
}