package dev.olog.presentation.prefs.blacklist

import androidx.core.view.isVisible
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.SimpleAdapter
import kotlinx.android.synthetic.main.dialog_blacklist_item.view.*

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

    override fun bind(holder: DataBoundViewHolder, item: BlacklistModel, position: Int) {
        holder.itemView.apply {
            BindingsAdapter.loadAlbumImage(holder.imageView!!, item.mediaId)
            scrim.isVisible = item.isBlacklisted
            firstText.text = item.title
            secondText.text = item.displayablePath
        }
    }

}