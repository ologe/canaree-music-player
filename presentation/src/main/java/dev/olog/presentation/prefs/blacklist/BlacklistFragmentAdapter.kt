package dev.olog.presentation.prefs.blacklist

import androidx.databinding.ViewDataBinding
import dev.olog.presentation.BR
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.SimpleAdapter

class BlacklistFragmentAdapter(
    data: List<BlacklistModel>
) : SimpleAdapter<BlacklistModel>(data.toMutableList()) {

    override fun getItemViewType(position: Int): Int = dataSet[position].type

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.itemView.setOnClickListener {
            getItem(viewHolder.adapterPosition)?.let { item ->
                item.isBlacklisted = !item.isBlacklisted
                notifyItemChanged(viewHolder.adapterPosition)
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: BlacklistModel, position: Int) {
        binding.setVariable(BR.item, item)
    }

}