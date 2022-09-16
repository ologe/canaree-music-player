package dev.olog.feature.library.folder

import android.view.View
import dev.olog.core.MediaId
import dev.olog.feature.media.api.MediaProvider
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.ui.model.DisplayableItem
import dev.olog.feature.library.R
import dev.olog.image.provider.BindingsAdapter
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.platform.adapter.setOnLongClickListener
import dev.olog.ui.model.DiffCallbackDisplayableItem
import dev.olog.ui.model.DisplayableAlbum
import dev.olog.ui.model.DisplayableHeader
import dev.olog.ui.model.DisplayableNestedListPlaceholder
import dev.olog.ui.model.DisplayableTrack
import kotlinx.android.synthetic.main.item_folder_tree_directory.view.*
import java.io.File

class FolderTreeFragmentAdapter(
    private val viewModel: FolderTreeFragmentViewModel,
    private val mediaProvider: MediaProvider,
    private val onItemLongClick: (View, MediaId) -> Unit,
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_folder_tree_directory,
            R.layout.item_folder_tree_track -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    when {
//                        item.mediaId == FolderTreeFragmentViewModel.BACK_HEADER_ID -> viewModel.popFolder() todo restore back button?
                        item is DisplayableAlbum && File(item.subtitle).isDirectory -> viewModel.nextFolder(File(item.subtitle))
                        item is DisplayableTrack -> {
                            mediaProvider.playFromMediaId(item.mediaId, null)
                        }
                    }
                }
                viewHolder.setOnLongClickListener(this) { item, _, view ->
//                    if (item.mediaId == FolderTreeFragmentViewModel.BACK_HEADER_ID) {
//                        return@setOnLongClickListener
//                    }
                    if (item is DisplayableTrack) {
                        onItemLongClick(view, item.mediaId)
                    }
                }
            }
        }

    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        holder.itemView.apply {
            firstText.text = when (item) {
                is DisplayableAlbum -> item.title
                is DisplayableHeader -> item.title
                is DisplayableNestedListPlaceholder -> TODO()
                is DisplayableTrack -> item.title
            }
        }
        when (holder.itemViewType){
            R.layout.item_folder_tree_directory -> {
                BindingsAdapter.loadSongImage(holder.imageView!!, item.mediaId)
            }
            R.layout.item_folder_tree_track -> {
                BindingsAdapter.loadSongImage(holder.imageView!!, item.mediaId)
            }
        }
    }
}