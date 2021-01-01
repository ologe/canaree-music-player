package dev.olog.feature.library.blacklist

import android.os.Environment
import dev.olog.core.mediaid.MediaId

internal data class BlacklistFragmentModel(
    val mediaId: MediaId,
    val title: String,
    val path: String,
    var isBlacklisted: Boolean // TODO made immutable
) {

    companion object {
        @Suppress("DEPRECATION")
        private val defaultStorageDir = Environment.getExternalStorageDirectory().path ?: "/storage/emulated/0/"
    }

    // show the path without "/storage/emulated/0"
    val displayablePath : String
        get() {
            return try {
                path.substring(defaultStorageDir.length)
            } catch (ex: StringIndexOutOfBoundsException){
                ex.printStackTrace()
                path
            }
        }

}