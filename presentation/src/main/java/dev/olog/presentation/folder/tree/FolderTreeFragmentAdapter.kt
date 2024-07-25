package dev.olog.presentation.folder.tree

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.base.adapter.ComposeListAdapter
import dev.olog.presentation.base.adapter.ComposeViewHolder
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.compose.list.ListItemHeader
import dev.olog.shared.compose.list.ListItemSong
import java.io.File

class FolderTreeFragmentAdapter(
    private val viewModel: FolderTreeFragmentViewModel,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator
) : ComposeListAdapter<FolderTreeItem>() {

    override fun bind(holder: ComposeViewHolder, item: FolderTreeItem, position: Int) {
        holder.setContent {
            when (item) {
                FolderTreeItem.Back -> ListItemSong(
                    leadingContent = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon( // TODO improve icon
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }, // TODO use different icon?
                    title = "...",
                    subtitle = null,
                    onClick = viewModel::popFolder,
                    onLongClick = {}
                )
                is FolderTreeItem.Header -> ListItemHeader(item.title)
                is FolderTreeItem.File -> {
                    ListItemSong(
                        mediaId = item.mediaId,
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = {
                            mediaProvider.playFromMediaId(item.mediaId, null, null)
                        },
                        onLongClick = {
                            navigator.toDialog(item.mediaId, holder.itemView)
                        }
                    )
                }
                is FolderTreeItem.Folder -> {
                    ListItemSong(
                        mediaId = item.mediaId,
                        title = item.title,
                        subtitle = null,
                        onClick = { viewModel.nextFolder(File(item.path)) },
                        onLongClick = null
                    )
                }
            }
        }
    }

}

@Stable
sealed interface FolderTreeItem {

    @Stable
    object Back: FolderTreeItem

    @Stable
    data class Header(val title: String): FolderTreeItem

    @Stable
    data class Folder(
        val mediaId: MediaId,
        val title: String,
        val path: String
    ): FolderTreeItem

    @Stable
    data class File(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String,
        val path: String
    ): FolderTreeItem

}