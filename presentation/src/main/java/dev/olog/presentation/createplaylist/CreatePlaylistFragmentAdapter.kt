package dev.olog.presentation.createplaylist

import androidx.compose.runtime.Composable
import dev.olog.presentation.createplaylist.mapper.CreatePlaylistFragmentItem
import dev.olog.shared.compose.component.CheckBox
import dev.olog.shared.compose.component.ComposeListAdapter
import dev.olog.shared.compose.component.ComposeViewHolder
import dev.olog.shared.compose.listitem.ListItemTrack

class CreatePlaylistFragmentAdapter(
    private val viewModel: CreatePlaylistFragmentViewModel
) : ComposeListAdapter<CreatePlaylistFragmentItem>(CreatePlaylistFragmentItem) {

    @Composable
    override fun Content(viewHolder: ComposeViewHolder, item: CreatePlaylistFragmentItem) {
        ListItemTrack(
            mediaId = item.mediaId,
            title = item.title,
            subtitle = item.subtitle,
            leadingContent = {
                CheckBox(
                    isChecked = item.isChecked,
                    onCheckedChange = {
                        viewModel.toggleItem(item.mediaId)
                    }
                )
            },
            onClick = {
                viewModel.toggleItem(item.mediaId)
            },
            onLongClick = null,
        )
    }

}