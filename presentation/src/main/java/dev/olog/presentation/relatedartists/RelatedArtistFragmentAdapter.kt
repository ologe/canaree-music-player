package dev.olog.presentation.relatedartists

import androidx.compose.runtime.Composable
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.compose.component.ComposeListAdapter
import dev.olog.shared.compose.component.ComposeViewHolder
import dev.olog.shared.compose.listitem.ListItemAlbum

class RelatedArtistFragmentAdapter(
    private val navigator: Navigator
) : ComposeListAdapter<RelatedArtistItem>(RelatedArtistItem) {

    @Composable
    override fun Content(viewHolder: ComposeViewHolder, item: RelatedArtistItem) {
        ListItemAlbum(
            mediaId = item.mediaId,
            title = item.title,
            subtitle = item.subtitle,
            onClick = {
                navigator.toDetailFragment(item.mediaId)
            },
            onLongClick = {
                navigator.toDialog(item.mediaId, viewHolder.itemView)
            }
        )
    }


}