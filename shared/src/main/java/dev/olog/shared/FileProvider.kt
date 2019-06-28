package dev.olog.shared

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object FileProvider {

    private const val authority = "${BuildConfig.APPLICATION_ID}.fileprovider"

    fun getUriForFile(context: Context, file: File): Uri {
        return try {
            FileProvider.getUriForFile(context, authority, file)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return Uri.EMPTY
        }
    }

    fun getUriForPath(context: Context, path: String): Uri {
        return getUriForFile(context, File(path))
    }

}