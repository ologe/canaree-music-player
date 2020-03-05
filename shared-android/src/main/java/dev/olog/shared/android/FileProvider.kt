package dev.olog.shared.android

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.File

object FileProvider {

    private const val authority = "dev.olog.msc.fileprovider"

    @JvmStatic
    fun getUriForFile(context: Context, file: File): Uri {
        return try {
            FileProvider.getUriForFile(context, authority, file)
        } catch (ex: Exception) {
            Timber.e(ex)
            return Uri.EMPTY
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmStatic
    inline fun getUriForPath(context: Context, path: String): Uri {
        return getUriForFile(context, File(path))
    }

}