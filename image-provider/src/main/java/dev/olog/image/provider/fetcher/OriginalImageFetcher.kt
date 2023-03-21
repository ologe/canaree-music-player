package dev.olog.image.provider.fetcher

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.provider.MediaStore.Audio.*
import dev.olog.core.entity.track.Song
import org.jaudiotagger.audio.mp3.MP3File
import java.io.*
import java.lang.IllegalArgumentException

object OriginalImageFetcher {

    private val NAMES = arrayOf("folder", "cover", "album")
    private val EXTENSIONS = arrayOf("jpg", "jpeg", "png")

    fun loadImage(context: Context, song: Song): InputStream? {
        val retriever = MediaMetadataRetriever()
        val uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, song.id)
        val fd = try {
            context.contentResolver.openFileDescriptor(uri, "r")
        } catch (ex: FileNotFoundException) {
            null
        }
        try {
            if (fd != null){
                retriever.setDataSource(fd.fileDescriptor) // time consuming
            } else {
                retriever.setDataSource(song.path)
            }
        } catch (ignored: IllegalArgumentException) { }

        val picture = retriever.embeddedPicture
        val result = picture?.let { ByteArrayInputStream(it) } ?: fallback(song.path)

        try {
            fd?.close()
        } catch (ignored: IOException) {}
        try {
            retriever.release()
        } catch (ignore: IOException) { }

        return result
    }

    private fun fallback(path: String): InputStream? {
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
            ex.printStackTrace()
        }

        val file = File(path).parentFile?.listFiles()
            ?.asSequence()
            ?.filter { !it.isDirectory }
            ?.filter { EXTENSIONS.contains(it.extension) }
            ?.find { NAMES.contains(it.nameWithoutExtension.lowercase()) }
        if (file != null) {
            return FileInputStream(file)
        }
        return null
    }

}