package dev.olog.presentation.prefs.blacklist

import dev.olog.core.MediaId
import dev.olog.presentation.base.adapter.ComposeListAdapter
import dev.olog.presentation.base.adapter.ComposeViewHolder
import dev.olog.shared.compose.list.ListItemBlacklist

class BlacklistFragmentAdapter(
    private val updateBlacklistState: (MediaId, Boolean) -> Unit,
) : ComposeListAdapter<BlacklistItem>() {

    override fun bind(holder: ComposeViewHolder, item: BlacklistItem, position: Int) {
        holder.setContent {
            ListItemBlacklist(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.displayablePath,
                isBlacklisted = item.isBlacklisted,
                onClick = {
                    updateBlacklistState(item.mediaId, !item.isBlacklisted)
                }
            )
        }
    }

}