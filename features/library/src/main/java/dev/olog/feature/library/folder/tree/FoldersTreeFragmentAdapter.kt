package dev.olog.feature.library.folder.tree

import dev.olog.feature.library.R
import dev.olog.feature.library.model.DiffCallbackDisplayableFile
import dev.olog.feature.library.model.DisplayableFile
import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.feature.presentation.base.adapter.ObservableAdapter
import dev.olog.feature.presentation.base.adapter.setOnClickListener
import dev.olog.feature.presentation.base.adapter.setOnLongClickListener
import dev.olog.feature.presentation.base.loadSongImage
import dev.olog.feature.presentation.base.model.toDomain
import dev.olog.lib.media.MediaProvider
import dev.olog.navigation.Navigator
import kotlinx.android.synthetic.main.item_folder_tree_track.view.*

internal class FoldersTreeFragmentAdapter(
    private val viewModel: FoldersTreeFragmentViewModel,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator

) : ObservableAdapter<DisplayableFile>(DiffCallbackDisplayableFile) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_folder_tree_directory,
            R.layout.item_folder_tree_track -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    when {
                        item.isFile() && item.asFile().isDirectory -> viewModel.nextFolder(item.asFile())
                        else -> {
                            viewModel.createMediaId(item)?.let { mediaId ->
                                mediaProvider.playFromMediaId(mediaId.toDomain(), null, null)
                            }

                        }
                    }
                }
                viewHolder.setOnLongClickListener(this) { item, _, view ->
                    if (!item.asFile().isDirectory) {
                        viewModel.createMediaId(item)?.let { mediaId ->
                            navigator.toDialog(mediaId.toDomain(), view, viewHolder.itemView)
                        }
                    }
                }
            }
        }
    }


    override fun bind(holder: DataBoundViewHolder, item: DisplayableFile, position: Int) {
        holder.itemView.apply {
            firstText.text = item.title
            holder.imageView?.loadSongImage(item.mediaId.toDomain())
        }
    }
}