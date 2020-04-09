package dev.olog.service.music

import dev.olog.domain.MediaId
import dev.olog.service.music.model.MediaEntity

internal object MediaEntityMock {

    val entity = MediaEntity(
        1L,
        1,
        MediaId.SONGS_CATEGORY.playableItem(1),
        2L,
        3L,
        "tile",
        "artist",
        "album artist",
        "album",
        100,
        123,
        "/storage",
        1,
        21,
        false
    )

}