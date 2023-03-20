package dev.olog.presentation.folder.tree

import androidx.lifecycle.Lifecycle
import dev.olog.feature.media.api.MediaProvider
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.DiffCallbackDisplayableItem
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.presentation.base.adapter.setOnClickListener
import dev.olog.presentation.base.adapter.setOnLongClickListener
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.navigator.Navigator
import kotlinx.android.synthetic.main.item_folder_tree_directory.view.secondText
import kotlinx.android.synthetic.main.item_folder_tree_track.view.firstText

class FolderTreeFragmentAdapter(
    lifecycle: Lifecycle,
    private val viewModel: FolderTreeFragmentViewModel,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator

) : ObservableAdapter<DisplayableItem>(lifecycle, DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_folder_tree_directory,
            R.layout.item_folder_tree_track -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    when {
                        item is DisplayableAlbum -> viewModel.nextFolder(item.subtitle)
                        item is DisplayableTrack -> mediaProvider.playFromMediaId(item.mediaId, null, null)
                    }
                }
                viewHolder.setOnLongClickListener(this) { item, _, view ->
                    if (item is DisplayableTrack) {
                        navigator.toDialog(item.mediaId, view)
                    }
                }
            }
        }

    }

    override fun bind(
        holder: DataBoundViewHolder,
        item: DisplayableItem,
        position: Int
    ) = with(holder.itemView) {
        when (item) {
            is DisplayableAlbum -> {
                firstText.text = item.title
                secondText.text = item.subtitle
                BindingsAdapter.loadAlbumImage(holder.imageView!!, item.mediaId)
            }
            is DisplayableTrack -> {
                firstText.text = item.title
                secondText.text = item.subtitle
                BindingsAdapter.loadSongImage(holder.imageView!!, item.mediaId)
            }
            is DisplayableHeader -> {
                firstText.text = item.title
            }
            else -> Unit
        }
    }
}