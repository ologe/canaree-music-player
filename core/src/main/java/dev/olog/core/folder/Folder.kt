package dev.olog.core.folder

import dev.olog.core.MediaUri
import java.io.File

data class Folder(
    val uri: MediaUri,
    val directory: String,
    val songs: Int,
) {

    val title: String
        get() = File(directory).name ?: ""

}