package dev.olog.presentation.detail.adapter

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.media.MediaProvider
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator

class DetailRecentlyAddedAdapter(
    lifecycle: Lifecycle,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider

) : ObservableAdapter<DisplayableItem>(lifecycle,
    DiffCallbackDisplayableItem
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            mediaProvider.playRecentlyAdded(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            navigator.toDialog(item.mediaId, viewHolder.itemView)
        }

        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            navigator.toDialog(item.mediaId, view)
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

}