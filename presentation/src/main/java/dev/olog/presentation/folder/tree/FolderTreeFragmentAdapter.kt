package dev.olog.presentation.folder.tree

import dev.olog.feature.base.adapter.LayoutContainerViewHolder
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.adapter.setOnClickListener
import dev.olog.feature.base.adapter.setOnLongClickListener
import dev.olog.lib.media.MediaProvider
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableFile
import dev.olog.presentation.navigator.NavigatorLegacy
import kotlinx.android.synthetic.main.item_folder_tree_track.*

class FolderTreeFragmentAdapter(
    private val viewModel: FolderTreeFragmentViewModel,
    private val mediaProvider: MediaProvider,
    private val navigator: NavigatorLegacy
) : ObservableAdapter<DisplayableFile>(DiffCallbackDisplayableFile) {

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
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

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: DisplayableFile,
        position: Int
    ) = holder.bindView {

        firstText.text = item.title

        when (holder.itemViewType){
            R.layout.item_folder_tree_directory -> ImageLoader.loadDirImage(imageView!!, item.path)
            R.layout.item_folder_tree_track -> ImageLoader.loadFile(imageView!!, item.path)
        }
    }
}