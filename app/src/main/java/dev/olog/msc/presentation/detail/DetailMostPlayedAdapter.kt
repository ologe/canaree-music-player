package dev.olog.msc.presentation.detail

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener

class DetailMostPlayedAdapter(
        lifecycle: Lifecycle,
        private val navigator: Navigator,
        private val mediaProvider: MediaProvider

) : AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            mediaProvider.playMostPlayed(item.mediaId)
        }

        viewHolder.setOnLongClickListener(controller) { item, _, _ ->
            navigator.toDialog(item, viewHolder.itemView)
        }

        viewHolder.setOnClickListener(R.id.more, controller) { item, _, view ->
            navigator.toDialog(item, view)
        }
        viewHolder.elevateSongOnTouch()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int){
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.position, position)
    }

    override val extendAreItemTheSame = {
        oldPosition: Int, newPosition: Int, _: DisplayableItem, _: DisplayableItem ->
        oldPosition == newPosition
    }

}