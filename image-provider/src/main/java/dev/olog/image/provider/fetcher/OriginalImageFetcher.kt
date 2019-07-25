package dev.olog.image.provider.fetcher

import android.media.MediaMetadataRetriever
import androidx.core.util.Pools
import kotlinx.coroutines.yield
import org.jaudiotagger.audio.mp3.MP3File
import java.io.*

object OriginalImageFetcher {

    private val FALLBACKS = arrayOf("cover.jpg", "album.jpg", "folder.jpg", "cover.png", "album.png", "folder.png")

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

        val parent = File(path).parentFile
        for (fallback in FALLBACKS) {
            yield()
            val cover = File(parent, fallback)
            if (cover.exists()) {
                return FileInputStream(cover)
            }
        }
        return null
    }

}