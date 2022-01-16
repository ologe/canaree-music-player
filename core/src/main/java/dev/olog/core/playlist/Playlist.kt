package dev.olog.core.playlist

import dev.olog.core.MediaUri
import java.io.File

data class Playlist(
    val uri: MediaUri,
    val title: String,
    val size: Int, // TODO rename to playables?
    val path: String, // TODO made nullable
) {

    val isReadOnly: Boolean
        get() = File(path).extension.isBlank()

}