package dev.olog.msc.utils.k.extension

import android.content.Context
import android.net.Uri
import android.support.v4.content.FileProvider
import dev.olog.msc.constants.AppConstants
import java.io.File

fun Context.getUriForFile(file: File): Uri {
    try {
        return FileProvider.getUriForFile(this, AppConstants.FILE_PROVIDER_PATH, file)
    } catch (ex: Exception){
        return Uri.EMPTY
    }

}