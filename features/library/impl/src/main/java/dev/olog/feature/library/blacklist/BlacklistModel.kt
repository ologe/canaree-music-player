package dev.olog.feature.library.blacklist

import android.os.Environment
import dev.olog.core.MediaId

data class BlacklistModel(
    val mediaId: MediaId,
    val title: String,
    val path: String,
    val isBlacklisted: Boolean
) {

    companion object {
        @Suppress("DEPRECATION")
        @JvmStatic
        private val defaultStorageDir =
            Environment.getExternalStorageDirectory().path ?: "/storage/emulated/0/"
    }

    // show the path without "/storage/emulated/0"
    val displayablePath: String
        get() {
            return try {
                path.substring(defaultStorageDir.length)
            } catch (ex: StringIndexOutOfBoundsException) {
                ex.printStackTrace()
                path
            }
        }

}