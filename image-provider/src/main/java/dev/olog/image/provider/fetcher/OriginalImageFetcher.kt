package dev.olog.image.provider.fetcher

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.ParcelFileDescriptor
import android.provider.MediaStore.Audio.Media
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.yield
import org.jaudiotagger.audio.mp3.MP3File
import timber.log.Timber
import java.io.*
import java.util.*

@Suppress("BlockingMethodInNonBlockingContext")
object OriginalImageFetcher {

    private val NAMES = arrayOf("folder", "cover", "album")
    private val EXTENSIONS = arrayOf("jpg", "jpeg", "png")

    suspend fun loadImage(context: Context, song: Song): InputStream? {
        var retriever: MediaMetadataRetriever? = null
        var fileDescriptor: ParcelFileDescriptor? = null
        return try {
            retriever = MediaMetadataRetriever().apply {
                val uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, song.id)
                fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                if (fileDescriptor != null) {
                    setDataSource(fileDescriptor!!.fileDescriptor) // time consuming
                } else {
                    setDataSource(song.path)
                }
            }
            yield()
            val picture = retriever.embeddedPicture
            yield()
            if (picture != null) {
                ByteArrayInputStream(picture)
            } else {
                fallback(song.path)
            }
        } finally {
            fileDescriptor?.close()
            retriever?.release()
        }
    }

    private suspend fun fallback(path: String): InputStream? {
        try {
            val mp3File = MP3File(path)
            if (mp3File.hasID3v2Tag()) {
                val art = mp3File.tag.firstArtwork
                if (art != null) {
                    val data = art.binaryData
                    return ByteArrayInputStream(data)
                }
            }
        } catch (ex: IOException) {
            Timber.w(ex)
            ex.printStackTrace()
        }
        yield()

        val file = File(path).parentFile?.listFiles()
            ?.asSequence()
            ?.filter { !it.isDirectory }
            ?.filter { EXTENSIONS.contains(it.extension) }
            ?.find { NAMES.contains(it.nameWithoutExtension.toLowerCase(Locale.getDefault())) }
        if (file != null) {
            return FileInputStream(file)
        }
        return null
    }

}