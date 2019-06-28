package dev.olog.msc.presentation.related.artists

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.msc.BR
import dev.olog.presentation.base.*
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator
import javax.inject.Inject

class RelatedArtistFragmentAdapter @Inject constructor(
    @FragmentLifecycle lifecycle: Lifecycle,
    private val navigator: Navigator

) : ObservableAdapter<DisplayableItem>(lifecycle, DiffCallbackDisplayableItem) {


    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            navigator.toDetailFragment(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }


}