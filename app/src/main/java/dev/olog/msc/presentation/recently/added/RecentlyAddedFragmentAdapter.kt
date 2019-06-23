package dev.olog.msc.presentation.recently.added

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.presentation.base.DataBoundViewHolder
import dev.olog.media.MediaProvider
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import javax.inject.Inject

class RecentlyAddedFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val navigator: Navigator,
        private val mediaProvider: MediaProvider

) : AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            mediaProvider.playFromMediaId(item.mediaId)
        }
        viewHolder.setOnLongClickListener(controller) { item, _, _ ->
            navigator.toDialog(item, viewHolder.itemView)
        }
        viewHolder.setOnClickListener(R.id.more, controller) { item, _, view ->
            navigator.toDialog(item, view)
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
        return viewType == R.layout.item_recently_added
    }

    override val onSwipeLeftAction = { position: Int ->
        controller.getItem(position)?.let { mediaProvider.addToPlayNext(it.mediaId) } ?: Any()
    }

}