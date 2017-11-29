package dev.olog.presentation.fragment_queue

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.presentation.BR
import dev.olog.presentation._base.BaseListAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.music_service.MusicController
import dev.olog.presentation.utils.setOnClickListener
import javax.inject.Inject

class PlayingQueueAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val musicController: MusicController

): BaseListAdapter<List<DisplayableItem>>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        viewHolder.setOnClickListener(getDataSet(), { item, _ ->
            musicController.skipToQueueItem(item.mediaId)
        })
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.source, 2)
        binding.setVariable(BR.position, position)
    }

    override fun hasGranularUpdate(): Boolean = false

}