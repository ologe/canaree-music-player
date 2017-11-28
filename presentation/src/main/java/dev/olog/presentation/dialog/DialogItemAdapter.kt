package dev.olog.presentation.dialog

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.presentation.BR
import dev.olog.presentation._base.BaseAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.model.DisplayableItem

class DialogItemAdapter(
        lifecycle: Lifecycle

) : BaseAdapter(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {

    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }
}