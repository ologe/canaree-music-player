package dev.olog.presentation.createplaylist


import android.widget.CheckBox
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.DisplayableItemDiffCallback2
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.presentation.base.adapter.setOnClickListener
import dev.olog.presentation.model.DisplayableItem2

class CreatePlaylistFragmentAdapter(
    lifecycle: Lifecycle,
    private val viewModel: CreatePlaylistFragmentViewModel

) : ObservableAdapter<DisplayableItem2>(lifecycle, DisplayableItemDiffCallback2) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, position, view ->
            val checkBox = view.findViewById<CheckBox>(R.id.selected)
            val wasChecked = checkBox.isChecked
            checkBox.isChecked = !wasChecked
            viewModel.toggleItem(item.mediaId)
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem2, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.isChecked, viewModel.isChecked(item.mediaId))

    }
}