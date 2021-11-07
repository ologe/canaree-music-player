package dev.olog.feature.library.folder.tree

import android.view.View
import androidx.lifecycle.Lifecycle
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.feature.base.BindingsAdapter
import dev.olog.feature.base.adapter.DataBoundViewHolder
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.adapter.setOnClickListener
import dev.olog.feature.base.adapter.setOnLongClickListener
import dev.olog.feature.base.model.DisplayableFile
import dev.olog.feature.library.R
import kotlinx.android.synthetic.main.item_folder_tree_track.view.*

class FolderTreeFragmentAdapter(
    lifecycle: Lifecycle,
    private val viewModel: FolderTreeFragmentViewModel,
    private val mediaProvider: MediaProvider,
    private val onItemLongClick: (MediaId, View) -> Unit,
) : ObservableAdapter<DisplayableFile>(lifecycle, DiffCallbackDisplayableFile) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_folder_tree_directory,
            R.layout.item_folder_tree_track -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    when {
                        item.mediaId == FolderTreeFragmentViewModel.BACK_HEADER_ID -> viewModel.popFolder()
                        item.isFile() && item.asFile().isDirectory -> viewModel.nextFolder(item.asFile())
                        else -> {
                            viewModel.createMediaId(item)?.let { mediaId ->
                                mediaProvider.playFromMediaId(mediaId, null, null)
                            }

                        }
                    }
                }
                viewHolder.setOnLongClickListener(this) { item, _, view ->
                    if (item.mediaId == FolderTreeFragmentViewModel.BACK_HEADER_ID) {
                        return@setOnLongClickListener
                    }
                    if (!item.asFile().isDirectory) {
                        viewModel.createMediaId(item)?.let { mediaId ->
                            onItemLongClick(mediaId, view)
                        }
                    }
                }
            }
        }

    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableFile, position: Int) {
        holder.itemView.apply {
            firstText.text = item.title
        }
        when (holder.itemViewType){
            R.layout.item_folder_tree_directory -> {
                BindingsAdapter.loadDirImage(holder.imageView!!, item)
            }
            R.layout.item_folder_tree_track -> {
                BindingsAdapter.loadFile(holder.imageView!!, item)
            }
        }
    }
}