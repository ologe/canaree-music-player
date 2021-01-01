package dev.olog.feature.player.player

import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.player.R
import dev.olog.lib.media.model.PlayerItem

sealed class PlayerFragmentModel {

    abstract val layoutType: Int

    data class Content(
        override val layoutType: Int
    ) : PlayerFragmentModel()

    data class MiniQueueItem(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String,
        val serviceProgressive: Long,
    ) : PlayerFragmentModel() {
        override val layoutType: Int = R.layout.item_mini_queue
    }

    object LoadMoreFooter : PlayerFragmentModel() {
        override val layoutType: Int = R.layout.item_mini_queue_load_more
    }

}

internal fun PlayerItem.toDisplayableItem(): PlayerFragmentModel {
    return PlayerFragmentModel.MiniQueueItem(
        mediaId = mediaId,
        title = title,
        subtitle = subtitle,
        serviceProgressive = serviceProgressive,
    )
}