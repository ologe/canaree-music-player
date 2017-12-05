package dev.olog.presentation.fragment_queue

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseListAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_music.MusicController
import dev.olog.presentation.utils.extension.setOnClickListener
import dev.olog.presentation.utils.extension.setOnLongClickListener
import javax.inject.Inject

class PlayingQueueAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val musicController: MusicController,
        private val navigator: Navigator

): BaseListAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        viewHolder.setOnClickListener(dataController) { item, _ ->
            musicController.skipToQueueItem(item.mediaId)
        }
        viewHolder.setOnLongClickListener(dataController) { item, _ ->
            navigator.toDialog(item, viewHolder.itemView)
        }
        viewHolder.setOnClickListener(R.id.more, dataController) { item, _, view ->
            navigator.toDialog(item, view)
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.source, 2)
        binding.setVariable(BR.position, position)
    }

    override val hasGranularUpdate: Boolean = false

    override fun getItemViewType(position: Int): Int = dataController[position].type

    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }
}