package dev.olog.feature.library.folder.tree

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.base.adapter.LayoutContainerViewHolder
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.adapter.setOnClickListener
import dev.olog.feature.base.adapter.setOnLongClickListener
import dev.olog.feature.library.R
import dev.olog.lib.media.MediaProvider
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.navigation.Navigator
import dev.olog.shared.exhaustive
import kotlinx.android.synthetic.main.item_folder_tree_track.*

class FolderTreeFragmentAdapter(
    private val viewModel: FolderTreeFragmentViewModel,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
) : ObservableAdapter<FolderTreeFragmentModel>(FolderTreeFragmentModelDiff) {

    override fun getItemViewType(position: Int): Int = getItem(position).layoutType

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_folder_tree_directory,
            R.layout.item_folder_tree_track -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    @Suppress("IMPLICIT_CAST_TO_ANY")
                    when (item) {
                        is FolderTreeFragmentModel.Back -> viewModel.popFolder()
                        is FolderTreeFragmentModel.Track -> {
                            val mediaId = viewModel.createMediaId(item) ?: return@setOnClickListener
                            mediaProvider.playFromMediaId(mediaId, null, null)
                        }
                        is FolderTreeFragmentModel.Directory -> viewModel.nextFolder(item.file)
                        is FolderTreeFragmentModel.Header -> error("invalid item=$item")
                    }.exhaustive
                }
                viewHolder.setOnLongClickListener(this) { item, _, view ->
                    when (item) {
                        is FolderTreeFragmentModel.Track -> {
                            val mediaId = viewModel.createMediaId(item) ?: return@setOnLongClickListener
                            navigator.toDialog(mediaId, view)
                        }
                        is FolderTreeFragmentModel.Back,
                        is FolderTreeFragmentModel.Directory -> {}
                        is FolderTreeFragmentModel.Header -> error("invalid item=$item")
                    }.exhaustive
                    if (item is FolderTreeFragmentModel.Back) {
                        return@setOnLongClickListener
                    }
                }
            }
        }

    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: FolderTreeFragmentModel,
        position: Int
    ) {

        when (item) {
            is FolderTreeFragmentModel.Back -> bindHeader(holder, "...")
            is FolderTreeFragmentModel.Header -> bindHeader(holder, item.title)
            is FolderTreeFragmentModel.Track -> bindTrack(holder, item)
            is FolderTreeFragmentModel.Directory -> bindDirectory(holder, item)
        }.exhaustive
    }

    private fun bindHeader(
        holder: LayoutContainerViewHolder,
        title: String
    ) = holder.bindView {
        firstText.text = title
    }

    private fun bindTrack(
        holder: LayoutContainerViewHolder,
        item: FolderTreeFragmentModel.Track,
    ) = holder.bindView {
        firstText.text = item.title
        ImageLoader.loadFile(imageView!!, item.path)
    }

    private fun bindDirectory(
        holder: LayoutContainerViewHolder,
        item: FolderTreeFragmentModel.Directory,
    ) = holder.bindView {
        firstText.text = item.title
        ImageLoader.loadDirImage(imageView!!, item.path)
    }

}

private object FolderTreeFragmentModelDiff : DiffUtil.ItemCallback<FolderTreeFragmentModel>() {

    override fun areItemsTheSame(oldItem: FolderTreeFragmentModel, newItem: FolderTreeFragmentModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: FolderTreeFragmentModel, newItem: FolderTreeFragmentModel): Boolean {
        return oldItem == newItem
    }
}