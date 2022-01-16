package dev.olog.core.genre

import dev.olog.core.MediaUri

data class Genre(
    val uri: MediaUri,
    val name: String,
    val songs: Int,
)