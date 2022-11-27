package dev.olog.image.provider.fetcher.internal

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.provider.MediaStore.Audio.Media
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.Song
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.audio.mp3.MP3File
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import javax.inject.Inject

internal class OriginalImageFetcher @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val NAMES = arrayOf("folder", "cover", "album")
    private val EXTENSIONS = arrayOf("jpg", "jpeg", "png")

    fun load(model: Song): InputStream? {
        var retriever: MediaMetadataRetriever? = null
        try {
            retriever = MediaMetadataRetriever().apply {
                val uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, model.id)
                setDataSource(context, uri)
            }
            val picture = retriever.embeddedPicture
            if (picture != null) {
                return ByteArrayInputStream(picture)
            }
        } finally {
            retriever?.release()
        }

        return fallback(model.path)
    }

    private fun fallback(path: String): InputStream? {
        try {
            val mp3File = MP3File(File(path), MP3File.LOAD_ALL, true)
            if (mp3File.hasID3v2Tag()) {
                val art = mp3File.tag.firstArtwork
                if (art != null) {
                    val data = art.binaryData
                    return ByteArrayInputStream(data)
                }
            }
        } catch (ex: InvalidAudioFrameException) {
            return null
        }


        return File(path).parentFile?.listFiles()
            ?.asSequence()
            ?.filter { !it.isDirectory }
            ?.filter { EXTENSIONS.contains(it.extension) }
            ?.find { NAMES.contains(it.nameWithoutExtension.lowercase(Locale.getDefault())) }
            ?.let { FileInputStream(it) }
    }

}