package dev.olog.presentation.recentlyadded

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.media.MediaProvider
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation.base.*
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator
import javax.inject.Inject

class RecentlyAddedFragmentAdapter @Inject constructor(
    @FragmentLifecycle lifecycle: Lifecycle,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider

) : ObservableAdapter<DisplayableItem>(lifecycle, DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playFromMediaId(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item, viewHolder.itemView)
        }
        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            navigator.toDialog(item, view)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

//    override fun canInteractWithViewHolder(viewType: Int): Boolean? { TODO
//        return viewType == R.layout.item_recently_added
//    }
//
//    override val onSwipeLeftAction = { position: Int ->
//        controller.getItem(position)?.let { mediaProvider.addToPlayNext(it.mediaId) } ?: Any()
//    }

}