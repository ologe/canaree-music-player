package dev.olog.platform

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object FileProvider {

    private const val authority = "dev.olog.msc.fileprovider"

    fun getUriForFile(context: Context, file: File): Uri {
        return try {
            FileProvider.getUriForFile(context, authority, file)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return Uri.EMPTY
        }
    }

    fun getUriForPath(context: Context, path: String): Uri {
        return getUriForFile(context, File(path))
    }

}