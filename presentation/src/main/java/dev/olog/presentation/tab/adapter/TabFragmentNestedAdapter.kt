package dev.olog.presentation.tab.adapter

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.presentation.BR
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem2
import dev.olog.presentation.navigator.Navigator

internal class TabFragmentNestedAdapter(
    @FragmentLifecycle lifecycle: Lifecycle,
    private val navigator: Navigator

) : ObservableAdapter<DisplayableItem2>(
    lifecycle,
    DisplayableItemDiffCallback2
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            navigator.toDetailFragment(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }
        viewHolder.elevateAlbumOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem2, position: Int) {
        binding.setVariable(BR.item, item)
    }

}