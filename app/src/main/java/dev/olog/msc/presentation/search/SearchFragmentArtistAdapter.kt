package dev.olog.msc.presentation.search

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.msc.BR
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.base.adp.AbsAdapter
import dev.olog.msc.presentation.base.adp.DataBoundViewHolder
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.k.extension.elevateAlbumOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import javax.inject.Inject

@PerFragment
class SearchFragmentArtistAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val navigator: Navigator,
        private val viewModel: SearchFragmentViewModel

) : AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            navigator.toDetailFragment(item.mediaId)
            viewModel.insertArtistToRecent(item.mediaId)
                    .subscribe({}, Throwable::printStackTrace)
        }
        viewHolder.setOnLongClickListener(controller) { item, _, _ ->
            navigator.toDialog(item, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

}