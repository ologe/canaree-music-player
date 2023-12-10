package dev.olog.presentation.folder.tree

import androidx.compose.runtime.Composable
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.media.MediaProvider
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.compose.component.ComposeListAdapter
import dev.olog.shared.compose.component.ComposeViewHolder
import dev.olog.shared.compose.listitem.ListItemHeader
import dev.olog.shared.compose.listitem.ListItemTrack
import java.io.File

class FolderTreeFragmentAdapter(
    private val viewModel: FolderTreeFragmentViewModel,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator
) : ComposeListAdapter<FolderTreeFragmentItem>(FolderTreeFragmentItem) {

    @Composable
    override fun Content(viewHolder: ComposeViewHolder, item: FolderTreeFragmentItem) {
        when (item) {
            is FolderTreeFragmentItem.Back -> {
                ListItemTrack(
                    mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, ""),
                    title = "...",
                    subtitle = null,
                    onClick = { viewModel.popFolder() },
                    onLongClick = null,
                )
            }
            is FolderTreeFragmentItem.Header -> {
                ListItemHeader(title = item.text)
            }
            is FolderTreeFragmentItem.Directory -> ListItemTrack(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = null,
                onClick = {
                    viewModel.nextFolder(File(item.path))
                },
                onLongClick = null, // TODO why not dialog?
            )
            is FolderTreeFragmentItem.Track -> ListItemTrack(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = null,
                onClick = {
                    viewModel.createMediaId(item)?.let { mediaId ->
                        mediaProvider.playFromMediaId(mediaId, null, null)
                    }
                },
                onLongClick = {
                    viewModel.createMediaId(item)?.let { mediaId ->
                        navigator.toDialog(mediaId, viewHolder.itemView)
                    }
                }
            )
        }
    }

}