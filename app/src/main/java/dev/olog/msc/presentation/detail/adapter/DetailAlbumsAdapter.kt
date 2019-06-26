package dev.olog.msc.presentation.detail.adapter

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.msc.BR
import dev.olog.msc.utils.k.extension.elevateAlbumOnTouch
import dev.olog.presentation.base.DataBoundViewHolder
import dev.olog.presentation.base.ObservableAdapter
import dev.olog.presentation.base.setOnClickListener
import dev.olog.presentation.base.setOnLongClickListener
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator

class DetailAlbumsAdapter (
        lifecycle: Lifecycle,
        private val navigator: Navigator

) : ObservableAdapter<DisplayableItem>(lifecycle){

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _,_ ->
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