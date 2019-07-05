package dev.olog.presentation.thanks

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import dev.olog.presentation.BR
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.presentation.model.SpecialThanksModel

class SpecialThanksFragmentAdapter(
    lifecycle: Lifecycle
) : ObservableAdapter<SpecialThanksModel>(lifecycle,
    DiffUtilSpecialThansModel
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
    }

    override fun bind(binding: ViewDataBinding, item: SpecialThanksModel, position: Int) {
        binding.setVariable(BR.item, item)
    }

}

object DiffUtilSpecialThansModel : DiffUtil.ItemCallback<SpecialThanksModel>() {
    override fun areItemsTheSame(
        oldItem: SpecialThanksModel,
        newItem: SpecialThanksModel
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: SpecialThanksModel,
        newItem: SpecialThanksModel
    ): Boolean {
        return oldItem == newItem
    }
}