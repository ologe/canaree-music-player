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
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import dev.olog.shared.android.utils.isQ
import java.io.File
import javax.inject.Inject

class MediaStoreQuery @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        private const val ALBUM_ARTIST_COLUMN = "album_artist"

        val audioUri: Uri = when {
            isQ() -> MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
    }

    fun queryAllAudio(): List<MediaStoreAudioEntity> {
        val sql = """
            SELECT 
                ${MediaStore.Audio.Media._ID},
                ${MediaStore.Audio.Media.ARTIST_ID},
                ${MediaStore.Audio.Media.ALBUM_ID},
                ${MediaStore.Audio.Media.DATA},
                ${MediaStore.Audio.Media.TITLE},
                ${MediaStore.Audio.Media.ARTIST},
                ${MediaStore.Audio.Media.ALBUM},
                ${ALBUM_ARTIST_COLUMN},
                ${MediaStore.Audio.Media.DURATION},
                ${MediaStore.Audio.Media.DATE_ADDED},
                ${MediaStore.Audio.Media.DATE_MODIFIED},
                ${MediaStore.Audio.Media.TRACK},
                ${MediaStore.Audio.Media.IS_PODCAST},
                ${MediaStore.Audio.Media.DISPLAY_NAME}
            FROM $audioUri
            WHERE (${MediaStore.Audio.Media.IS_MUSIC} != 0 OR ${MediaStore.Audio.Media.IS_PODCAST} != 0)
                AND ${MediaStore.Audio.Media.IS_NOTIFICATION} = 0
        """.trimIndent()
        return context.contentResolver.querySql(sql).mapToMediaStoreAudio()
    }

    private fun Cursor.mapToMediaStoreAudio(): List<MediaStoreAudioEntity> = use {
        val idColumn = getColumnIndex(MediaStore.Audio.Media._ID)
        val artistIdColumn = getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
        val albumIdColumn = getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
        val pathColumn = getColumnIndex(MediaStore.Audio.Media.DATA)
        val titleColumn = getColumnIndex(MediaStore.Audio.Media.TITLE)
        val artistColumn = getColumnIndex(MediaStore.Audio.Media.ARTIST)
        val albumColumn = getColumnIndex(MediaStore.Audio.Media.ALBUM)
        val albumArtistColumn = getColumnIndex(ALBUM_ARTIST_COLUMN)
        val durationColumn = getColumnIndex(MediaStore.Audio.Media.DURATION)
        val dateAddedColumn = getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
        val dateModifierColumn = getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)
        val trackColumn = getColumnIndex(MediaStore.Audio.Media.TRACK)
        val isPodcastColumn = getColumnIndex(MediaStore.Audio.Media.IS_PODCAST)
        val displayNameColumn = getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)

        buildList {
            while (moveToNext()) {
                val artist = getStringOrNull(artistColumn).orEmpty()
                val track = getIntOrNull(trackColumn) ?: 0
                val path = getStringOrNull(pathColumn).orEmpty()
                val directoryPath = if (path.isNotBlank()) {
                    path.substring(0, path.lastIndexOf(File.separator).coerceAtLeast(0))
                } else {
                    ""
                }
                val directoryName = directoryPath.substringAfterLast(
                    delimiter = File.separator,
                    missingDelimiterValue = directoryPath
                )

                this += MediaStoreAudioEntity(
                    id = getStringOrNull(idColumn) ?: continue,
                    artistId = getStringOrNull(artistIdColumn) ?: continue,
                    albumId = getStringOrNull(albumIdColumn) ?: continue,
                    title = getStringOrNull(titleColumn).orEmpty(),
                    artist = artist,
                    album = getStringOrNull(albumColumn).orEmpty(),
                    albumArtist = getStringOrNull(albumArtistColumn) ?: artist,
                    duration = getLongOrNull(durationColumn) ?: -1L,
                    dateAdded = getLongOrNull(dateAddedColumn) ?: -1L,
                    dateModified = getLongOrNull(dateModifierColumn) ?: -1L,
                    path = path,
                    directoryPath = directoryPath,
                    directoryName = directoryName,
                    discNumber = if (track >= 1000) track / 1000 else 0,
                    trackNumber = if (track >= 1000) track % 1000 else track,
                    isPodcast = getIntOrNull(isPodcastColumn) != 0,
                    displayName = getStringOrNull(displayNameColumn).orEmpty(),
                )
            }
        }
    }

}