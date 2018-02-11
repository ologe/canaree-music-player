package dev.olog.msc.presentation.library.tab

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.msc.BR
import dev.olog.msc.dagger.FragmentLifecycle
import dev.olog.msc.dagger.PerFragment
import dev.olog.msc.presentation.base.adapter.BaseListAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.k.extension.elevateAlbumOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import javax.inject.Inject

@PerFragment
class TabFragmentLastPlayedAlbumsAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val navigator: Navigator,
        private val viewModel: TabFragmentViewModel,
        private val mediaProvider: MediaProvider

): BaseListAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        viewHolder.setOnClickListener(dataController) { item, _ ->
            navigator.toDetailFragment(item.mediaId)
            viewModel.insertAlbumLastPlayed(item.mediaId)
                    .subscribe({}, Throwable::printStackTrace)
        }
        viewHolder.setOnLongClickListener(dataController) { item, _ ->
            navigator.toDialog(item, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
//        binding.setVariable(BR.musicController, musicController)
//        binding.setVariable(BR.quickAction, Constants.quickAction)
    }

}