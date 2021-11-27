package dev.olog.feature.library.blacklist

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.olog.feature.base.BindingsAdapter
import dev.olog.feature.base.adapter.SimpleViewHolder
import dev.olog.feature.library.R
import dev.olog.shared.android.extensions.inflate
import kotlinx.android.synthetic.main.dialog_blacklist_item.view.*

class BlacklistFragmentAdapter(
    private val toggleBlacklisted: (BlacklistModel) -> Unit,
) : ListAdapter<BlacklistModel, SimpleViewHolder>(BlacklistFragmentAdapter) {

    companion object : DiffUtil.ItemCallback<BlacklistModel>() {
        override fun areItemsTheSame(oldItem: BlacklistModel, newItem: BlacklistModel): Boolean {
            return oldItem.path == newItem.path 
        }

        override fun areContentsTheSame(oldItem: BlacklistModel, newItem: BlacklistModel): Boolean {
            return oldItem == newItem
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val view = parent.inflate(R.layout.dialog_blacklist_item)
        return SimpleViewHolder(view) {
            itemView.setOnClickListener {
                val item = getItem(bindingAdapterPosition)
                toggleBlacklisted(item)
            }
        }
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.apply {
            BindingsAdapter.loadAlbumImage(holder.imageView!!, item.mediaId)
            scrim.isVisible = item.isBlacklisted
            firstText.text = item.title
            secondText.text = item.displayablePath
        }
    }

}