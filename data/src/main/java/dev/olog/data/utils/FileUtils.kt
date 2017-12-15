package dev.olog.data.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import dev.olog.data.ImageUtils
import dev.olog.domain.entity.Song
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    fun saveFile(context: Context, parentFolder: String, fileName: String, bitmap: Bitmap): String {
        val parentFile = File("${context.applicationInfo.dataDir}${File.separator}$parentFolder")
        parentFile.mkdirs()
        val dest = File(parentFile, fileName)
        val out = FileOutputStream(dest)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.close()
        bitmap.recycle()
        return dest.path
    }

    fun playlistImagePath(context: Context, playlistId: Long): String {
        return "${context.applicationInfo.dataDir}${File.separator}playlist${File.separator}$playlistId"
    }

    fun genreImagePath(context: Context, genreId: Long): String {
        return "${context.applicationInfo.dataDir}${File.separator}genre${File.separator}$genreId"
    }

    fun makeImages(context: Context, songListFlowable: Flowable<List<Song>>, parentFolder: String, itemId: String): Maybe<Bitmap> {
        return songListFlowable.firstOrError()
                .map { songList -> songList.asSequence()
                        .map { it.albumId }
                        .map { ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), it) }
                        .map {
                            try {
                                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                            } catch (ex: Exception) {
                                null
                            }
                        }.filter { it != null }
                        .map { it!! }
                        .take(4)
                        .toList()
                }.filter { it.isNotEmpty() }
                .map { ImageUtils.joinImages(it) }
                .doOnSuccess { FileUtils.saveFile(context, parentFolder, itemId, it) }
                .subscribeOn(Schedulers.io())
    }

}