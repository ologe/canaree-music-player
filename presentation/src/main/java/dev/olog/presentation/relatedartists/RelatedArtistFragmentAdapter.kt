package dev.olog.presentation.relatedartists

import androidx.compose.runtime.Stable
import dev.olog.core.MediaId
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.compose.list.ListItemAlbum

class RelatedArtistFragmentAdapter(
    private val navigator: Navigator
) : ComposeListAdapter<RelatedArtistItem>() {

    override fun bind(holder: ComposeViewHolder, item: RelatedArtistItem, position: Int) {
        holder.setContent {
            ListItemAlbum(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.title,
                onClick = { navigator.toDetailFragment(item.mediaId) },
                onLongClick = { navigator.toDialog(item.mediaId, holder.itemView) }
            )
        }
    }


}

@Stable
data class RelatedArtistItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)