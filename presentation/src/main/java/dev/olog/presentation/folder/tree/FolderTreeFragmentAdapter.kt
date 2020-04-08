package dev.olog.presentation.folder.tree

import dev.olog.lib.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.feature.presentation.base.adapter.ObservableAdapter
import dev.olog.feature.presentation.base.adapter.setOnClickListener
import dev.olog.feature.presentation.base.adapter.setOnLongClickListener
import dev.olog.presentation.loadDirImage
import dev.olog.presentation.loadFile
import dev.olog.presentation.model.DisplayableFile
import dev.olog.presentation.navigator.Navigator
import dev.olog.feature.presentation.base.model.toDomain
import kotlinx.android.synthetic.main.item_detail_related_artist.view.*

internal class FolderTreeFragmentAdapter(
    private val viewModel: FolderTreeFragmentViewModel,
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
                            navigator.toDialog(mediaId, view, viewHolder.itemView)
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
                holder.imageView!!.loadDirImage(item)
            }
            R.layout.item_folder_tree_track -> {
                holder.imageView!!.loadFile(item)
            }
        }
    }
}