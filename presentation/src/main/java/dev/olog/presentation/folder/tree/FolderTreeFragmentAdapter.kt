package dev.olog.presentation.folder.tree

import androidx.lifecycle.Lifecycle
import dev.olog.media.MediaProvider
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.feature.base.adapter.DataBoundViewHolder
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.adapter.setOnClickListener
import dev.olog.feature.base.adapter.setOnLongClickListener
import dev.olog.presentation.model.DisplayableFile
import dev.olog.presentation.navigator.Navigator
import kotlinx.android.synthetic.main.item_detail_related_artist.view.*

class FolderTreeFragmentAdapter(
    lifecycle: Lifecycle,
    private val viewModel: FolderTreeFragmentViewModel,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator

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
                            navigator.toDialog(mediaId, view)
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