package dev.olog.image.provider.fetcher

import android.media.MediaMetadataRetriever
import kotlinx.coroutines.yield
import org.jaudiotagger.audio.mp3.MP3File
import java.io.*

object OriginalImageFetcher {

    private val NAMES = arrayOf("folder", "cover", "album")
    private val EXTENSIONS = arrayOf("jpg", "jpeg", "png")

    suspend fun loadImage(path: String): InputStream? {
        var retriever: MediaMetadataRetriever? = null
        return try {
            retriever = MediaMetadataRetriever().apply {
                setDataSource(path) // time consuming
            }
            yield()
            val picture = retriever.embeddedPicture
            yield()
            if (picture != null) {
                ByteArrayInputStream(picture)
            } else {
                fallback(path)
            }
        } finally {
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
            ex.printStackTrace()
        }

        val file = File(path).parentFile?.listFiles()
            ?.asSequence()
            ?.filter { !it.isDirectory }
            ?.filter { EXTENSIONS.contains(it.extension) }
            ?.find { NAMES.contains(it.nameWithoutExtension.toLowerCase()) }
        if (file != null) {
            return FileInputStream(file)
        }
        return null
    }

}