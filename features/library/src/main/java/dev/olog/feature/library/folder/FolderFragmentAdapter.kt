package dev.olog.feature.library.folder

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.feature.presentation.base.adapter.ObservableAdapter2
import dev.olog.feature.presentation.base.adapter.setOnClickListener
import dev.olog.feature.presentation.base.loadAlbumImage
import dev.olog.feature.presentation.base.loadSongImage
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.shared.exhaustive
import kotlinx.android.synthetic.main.item_breadcrumb.*
import kotlinx.android.synthetic.main.item_tab_album.*
import java.io.File

internal class FolderFragmentAdapter(
    private val viewModel: FolderFragmentViewModel
) : ObservableAdapter2<FolderFragmentItem>(FolderFragmentItemDiff) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_folder_tree_directory -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    require(item is FolderFragmentItem.Folder)
                    viewModel.updateFolder(item)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is FolderFragmentItem.BreadCrumb -> R.layout.item_breadcrumb
        is FolderFragmentItem.Header -> R.layout.item_folder_tree_header
        is FolderFragmentItem.Album -> R.layout.item_tab_album
        is FolderFragmentItem.File -> R.layout.item_folder_tree_track
        is FolderFragmentItem.Folder -> R.layout.item_folder_tree_directory
    }

    override fun bind(holder: DataBoundViewHolder, item: FolderFragmentItem, position: Int) {
        when (item) {
            is FolderFragmentItem.Album -> {
                holder.apply {
                    cover.loadAlbumImage(item.mediaId.toDomain())
                    firstText.text = item.title
                    secondText.text = item.subtitle
                }
            }
            is FolderFragmentItem.Folder -> {
                holder.apply {
                    cover.loadSongImage(item.mediaId.toDomain())
                    firstText.text = item.title
                }
            }
            is FolderFragmentItem.File -> {
                holder.apply {
                    cover.loadSongImage(item.mediaId.toDomain())
                    firstText.text = item.title
                }
            }
            is FolderFragmentItem.Header -> {
                holder.apply {
                    firstText.text = item.title
                }
            }
            is FolderFragmentItem.BreadCrumb -> {
                holder.apply {
                    bread_crumbs.setActiveOrAdd(item.file, false)
                    bread_crumbs.setCallback { crumb, _ ->
                        viewModel.updateFolder(crumb.file)
                    }
                }
            }
        }.exhaustive
    }
}

private object FolderFragmentItemDiff : DiffUtil.ItemCallback<FolderFragmentItem>() {

    override fun areItemsTheSame(
        oldItem: FolderFragmentItem,
        newItem: FolderFragmentItem
    ): Boolean {
        if (oldItem is FolderFragmentItem.Album && newItem is FolderFragmentItem.Album) {
            return oldItem.mediaId == newItem.mediaId
        }
        if (oldItem is FolderFragmentItem.File && newItem is FolderFragmentItem.File) {
            return oldItem.mediaId == newItem.mediaId
        }
        if (oldItem is FolderFragmentItem.Folder && newItem is FolderFragmentItem.Folder) {
            return oldItem.mediaId == newItem.mediaId
        }
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: FolderFragmentItem,
        newItem: FolderFragmentItem
    ): Boolean {
        return oldItem == newItem
    }
}