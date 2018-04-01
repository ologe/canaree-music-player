package dev.olog.msc.presentation.playlist.track.chooser

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.widget.CheckBox
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.k.extension.setOnClickListener
import javax.inject.Inject

class PlaylistTracksChooserFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val viewModel: PlaylistTracksChooserFragmentViewModel

) : AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, position, view ->
            val checkBox = view.findViewById<CheckBox>(R.id.selected)
            val wasChecked = checkBox.isChecked
            checkBox.isChecked = !wasChecked
            viewModel.toggleItem(item.mediaId)
//            notifyItemChanged(position)
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.isChecked, viewModel.isChecked(item.mediaId))

    }
}