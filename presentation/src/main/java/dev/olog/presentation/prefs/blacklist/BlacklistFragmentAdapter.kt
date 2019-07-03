package dev.olog.presentation.prefs.blacklist

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import dev.olog.presentation.BR
import dev.olog.presentation.base.DataBoundViewHolder
import dev.olog.presentation.base.ObservableAdapter
import dev.olog.presentation.base.setOnClickListener

class BlacklistFragmentAdapter(
    lifecycle: Lifecycle
) : ObservableAdapter<BlacklistModel>(lifecycle, DiffCallbackBlacklistModel) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            val position = viewHolder.adapterPosition
            item.isBlacklisted = !item.isBlacklisted
            notifyItemChanged(position)
        }
    }

    override fun bind(binding: ViewDataBinding, item: BlacklistModel, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.isBlacklisted, item.isBlacklisted)
    }

}

object DiffCallbackBlacklistModel : DiffUtil.ItemCallback<BlacklistModel>() {
    override fun areItemsTheSame(oldItem: BlacklistModel, newItem: BlacklistModel): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: BlacklistModel, newItem: BlacklistModel): Boolean {
        return oldItem == newItem
    }
}