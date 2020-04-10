package dev.olog.presentation.prefs.blacklist

import androidx.core.view.isVisible
import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.feature.presentation.base.adapter.SimpleAdapter
import dev.olog.feature.presentation.base.loadAlbumImage
import dev.olog.feature.presentation.base.model.toDomain
import kotlinx.android.synthetic.main.dialog_blacklist_item.view.*

class BlacklistFragmentAdapter : SimpleAdapter<BlacklistModel>() {

    override fun getItemViewType(position: Int): Int = getItem(position).type

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.itemView.setOnClickListener {
            // TODO, 3 states, unhide, hide this only, hide this and subfolders
            val item = getItem(viewHolder.adapterPosition)
            item.isBlacklisted = !item.isBlacklisted
            notifyItemChanged(viewHolder.adapterPosition)
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: BlacklistModel, position: Int) {
        holder.itemView.apply {
            holder.imageView!!.loadAlbumImage(item.mediaId.toDomain())
            scrim.isVisible = item.isBlacklisted
            firstText.text = item.title
            secondText.text = item.displayablePath
        }
    }

}