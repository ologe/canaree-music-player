package dev.olog.feature.player.player

import androidx.annotation.LayoutRes
import dev.olog.core.mediaid.MediaId
import dev.olog.feature.player.R
import dev.olog.lib.media.model.PlayerItem

sealed class PlayerFragmentModel(
    @LayoutRes open val layoutType: Int
) {

    data class Content(
        @LayoutRes override val layoutType: Int
    ) : PlayerFragmentModel(layoutType)

    data class MiniQueueItem(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String,
        val serviceProgressive: Long,
    ) : PlayerFragmentModel(R.layout.item_mini_queue)

    object LoadMoreFooter : PlayerFragmentModel(R.layout.item_mini_queue_load_more)

}

internal fun PlayerItem.toDisplayableItem(): PlayerFragmentModel {
    return PlayerFragmentModel.MiniQueueItem(
        mediaId = mediaId,
        title = title,
        subtitle = subtitle,
        serviceProgressive = serviceProgressive,
    )
}