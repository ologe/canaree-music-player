package dev.olog.msc

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.crashlytics.android.Crashlytics
import dev.olog.msc.domain.entity.Song
import java.io.File

object FileProvider {

    private const val authority = "${BuildConfig.APPLICATION_ID}.fileprovider"

    fun getUriForFile(context: Context, file: File): Uri {
        return try {
            FileProvider.getUriForFile(context, authority, file)
        } catch (ex: Exception){
            Crashlytics.logException(ex)
            return Uri.EMPTY
        }
    }

    fun getUriForPath(context: Context, path: String): Uri {
        return getUriForFile(context, File(path))
    }

    fun getUriForSong(context: Context, song: Song): Uri {
        return getUriForPath(context, song.path)
    }

}