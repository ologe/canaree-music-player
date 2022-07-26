package dev.olog.data.mediastore

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.contentresolversql.querySql
import dev.olog.shared.isQ
import java.io.File
import javax.inject.Inject

class MediaStoreQuery @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        val audioUri: Uri = when {
            isQ() -> MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
    }

    private val contextResolver = context.contentResolver

    fun queryAllAudio(): List<MediaStoreAudioEntity> {
        val sql = """
            SELECT 
                ${MediaStore.Audio.AudioColumns._ID},
                ${MediaStore.Audio.AudioColumns.ARTIST_ID},
                ${MediaStore.Audio.AudioColumns.ALBUM_ID},
                ${MediaStore.Audio.AudioColumns.DATA},
                ${MediaStore.Audio.AudioColumns.TITLE},
                ${MediaStore.Audio.AudioColumns.ARTIST},
                ${MediaStore.Audio.AudioColumns.ALBUM},
                ${MediaStoreExtraColumns.ALBUM_ARTIST},
                ${MediaStore.Audio.AudioColumns.DURATION},
                ${MediaStore.Audio.AudioColumns.DATE_ADDED},
                ${MediaStore.Audio.AudioColumns.TRACK},
                ${MediaStore.Audio.AudioColumns.IS_PODCAST},
                ${MediaStore.Audio.AudioColumns.DISPLAY_NAME}
            FROM $audioUri
            WHERE ${MediaStore.Audio.Media.IS_MUSIC} = 1 OR ${MediaStore.Audio.Media.IS_PODCAST} = 1
        """.trimIndent()
        return contextResolver.querySql(sql).mapToMediaStoreAudio()
    }

    private fun Cursor.mapToMediaStoreAudio(): List<MediaStoreAudioEntity> = use {
        val idColumn = getColumnIndex(MediaStore.Audio.AudioColumns._ID)
        val artistIdColumn = getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST_ID)
        val albumIdColumn = getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID)
        val pathColumn = getColumnIndex(MediaStore.MediaColumns.DATA)
        val titleColumn = getColumnIndex(MediaStore.MediaColumns.TITLE)
        val artistColumn = getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST)
        val albumColumn = getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)
        val albumArtistColumn = getColumnIndex(MediaStoreExtraColumns.ALBUM_ARTIST)
        val durationColumn = getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)
        val dateAddedColumn = getColumnIndex(MediaStore.MediaColumns.DATE_ADDED)
        val dateModifiedColumn = getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED)
        val trackColumn = getColumnIndex(MediaStore.Audio.AudioColumns.TRACK)
        val isPodcastColumn = getColumnIndex(MediaStore.Audio.AudioColumns.IS_PODCAST)
        val displayNameColumn = getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)

        buildList {
            while (moveToNext()) {
                val author = getStringOrNull(artistColumn).orEmpty()
                val track = getIntOrNull(trackColumn) ?: 0
                val path = getStringOrNull(pathColumn).orEmpty()
                this += MediaStoreAudioEntity(
                    id = getStringOrNull(idColumn) ?: continue,
                    artistId = getStringOrNull(artistIdColumn) ?: continue,
                    albumId = getStringOrNull(albumIdColumn) ?: continue,
                    title = getStringOrNull(titleColumn).orEmpty(),
                    artist = author,
                    album = getStringOrNull(albumColumn).orEmpty(),
                    albumArtist = getStringOrNull(albumArtistColumn) ?: author,
                    duration = getLongOrNull(durationColumn) ?: -1L,
                    dateAdded = getLongOrNull(dateAddedColumn) ?: -1L,
                    dateModified = getLongOrNull(dateModifiedColumn) ?: -1L,
                    path = path,
                    directory = if (path.isNotBlank()) {
                        path.substring(0, path.lastIndexOf(File.separator).coerceAtLeast(0))
                    } else {
                        ""
                    },
                    discNumber = if (track >= 1000) track / 1000 else 0,
                    trackNumber = if (track >= 1000) track % 1000 else track,
                    isPodcast = getIntOrNull(isPodcastColumn) != 0,
                    displayName = getStringOrNull(displayNameColumn).orEmpty(),
                )
            }
        }
    }

}