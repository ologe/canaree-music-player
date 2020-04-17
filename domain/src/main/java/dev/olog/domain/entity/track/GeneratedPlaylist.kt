package dev.olog.domain.entity.track

import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory

data class GeneratedPlaylist(
    val id: Long,
    val title: String,
    val size: Int
) {

    val mediaId: MediaId.Category
        get() {
            val category = MediaIdCategory.GENERATED_PLAYLIST
            return MediaId.Category(category, "${this.id}")
        }

}