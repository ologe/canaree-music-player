package dev.olog.feature.library.folder.tree.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.folder.FileType
import dev.olog.feature.base.BindingsAdapter
import dev.olog.feature.base.adapter.BaseAdapter
import dev.olog.feature.library.R
import dev.olog.shared.android.extensions.inflate
import dev.olog.shared.exhaustive

class FolderTreeFragmentItemAdapter(
    private val onItemClick: (FileType) -> Unit,
    private val onItemLongClick: (FileType, View) -> Unit,
) : BaseAdapter<FileType, FolderTreeFragmentItemAdapter.ViewHolder>(FolderTreeFragmentItemAdapter) {

    companion object : DiffUtil.ItemCallback<FileType>() {

        override fun areItemsTheSame(oldItem: FileType, newItem: FileType) = oldItem.path == newItem.path
        override fun areContentsTheSame(oldItem: FileType, newItem: FileType) = oldItem == newItem
        override fun getChangePayload(oldItem: FileType, newItem: FileType): Any = newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vh = ViewHolder(parent)
        vh.setupDefaultClickListeners(
            onClick = { item, _ -> onItemClick(item) },
            onLongClick = { item, _ -> onItemLongClick(item, vh.itemView) },
        )
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, item: FileType, position: Int) {
        holder.bind(item)
    }

    class ViewHolder(
        parent: ViewGroup
    ) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_folder_tree_item)) {

        private val image = itemView.findViewById<ImageView>(R.id.cover)
        private val title = itemView.findViewById<TextView>(R.id.firstText)

        fun bind(item: FileType) {
            when (item) {
                is FileType.Folder -> {
                    BindingsAdapter.loadDirImage(image, item)
                }
                is FileType.Track -> {
                    BindingsAdapter.loadFile(image, item)
                }
            }.exhaustive

            title.text = item.name
        }

    }

}