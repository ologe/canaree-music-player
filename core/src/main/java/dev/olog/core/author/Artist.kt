package dev.olog.core.author

import dev.olog.core.MediaUri

data class Artist(
    val uri: MediaUri,
    val name: String,
    val songs: Int,
)