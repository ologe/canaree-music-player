package dev.olog.core.collection

import dev.olog.core.MediaUri
import java.io.File

data class Album(
    val uri: MediaUri,
    val artistUri: MediaUri,
    val title: String,
    val artist: String,
    val songs: Int,
    private val directory: String,
) {

    val hasSameNameAsFolder: Boolean
        get() = title == (File(directory).name ?: "")

}