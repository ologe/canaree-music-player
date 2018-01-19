package dev.olog.presentation.fragment_player

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
import kotlinx.android.synthetic.main.item_mini_queue.view.*
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

                viewHolder.itemView.dragHandle.setOnTouchListener { _, event ->
                    if(event.actionMasked == MotionEvent.ACTION_DOWN) {
                        touchHelper()?.startDrag(viewHolder)
                        true
                    } else false
                }
                viewHolder.elevateSongOnTouch()
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    override fun hasGranularUpdate(): Boolean = hasGranularUpdate

    override val touchCallbackConfig = TouchCallbackConfig(
            true, true,
            draggableViewType = R.layout.item_mini_queue,
            onDragAction = { from, to ->
                musicController.swapRelative(from, to)
            }, onSwipeAction = { position -> musicController.removeRelative(position) }
    )
}