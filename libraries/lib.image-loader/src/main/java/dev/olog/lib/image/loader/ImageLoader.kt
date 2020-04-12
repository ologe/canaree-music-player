package dev.olog.lib.image.loader

import android.content.Context
import dev.olog.lib.image.loader.creator.ImagesFolderUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageLoader {

    suspend fun clearCache(context: Context) {
        GlideApp.get(context.applicationContext).clearMemory()

        withContext(Dispatchers.IO) {
            GlideApp.get(context.applicationContext).clearDiskCache()
            ImagesFolderUtils.getImageFolderFor(context, ImagesFolderUtils.FOLDER).listFiles()
                ?.forEach { it.delete() }
            ImagesFolderUtils.getImageFolderFor(context, ImagesFolderUtils.PLAYLIST).listFiles()
                ?.forEach { it.delete() }
            ImagesFolderUtils.getImageFolderFor(context, ImagesFolderUtils.GENRE).listFiles()
                ?.forEach { it.delete() }
        }
    }

}