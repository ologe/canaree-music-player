package dev.olog.presentation.prefs.blacklist

import android.widget.TextView
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.SimpleAdapter
import dev.olog.shared.android.extensions.toggleVisibility

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
        BindingsAdapter.loadAlbumImage(holder.imageView!!, item.mediaId)
        holder.itemView.findViewById<TextView>(R.id.scrim).toggleVisibility(item.isBlacklisted, true)
        holder.itemView.findViewById<TextView>(R.id.firstText).text = item.title
        holder.itemView.findViewById<TextView>(R.id.secondText).text = item.displayablePath
    }

}