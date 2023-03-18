package dev.olog.data.mediastore

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import android.provider.MediaStore.Audio.Media
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.platform.BuildVersion
import javax.inject.Inject

class MediaStoreQuery @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun queryAllAudio(): List<MediaStoreAudioEntity> {
        val uri: Uri = when {
            BuildVersion.isQ() -> Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else -> Media.EXTERNAL_CONTENT_URI
        }
        val cursor = context.contentResolver.query(uri, null, null, null, null) ?: return emptyList()
        try {
            val result = mutableListOf<MediaStoreAudioEntity>()

            val idColumn = cursor.getColumnIndexOrThrow(AudioColumns._ID)
            val albumColumn = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM)
            val albumArtistColumn = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM_ARTIST)
            val artistColumn = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST)
            val bitrateColumn = cursor.getColumnIndexOrThrow(AudioColumns.BITRATE)
            val bucketDisplayNameColumn = cursor.getColumnIndexOrThrow(AudioColumns.BUCKET_DISPLAY_NAME)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(AudioColumns.BUCKET_ID)
            val dataColumn = cursor.getColumnIndexOrThrow(AudioColumns.DATA)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(AudioColumns.DATE_ADDED)
            val dateModifiedColumn = cursor.getColumnIndexOrThrow(AudioColumns.DATE_MODIFIED)
            val displayNameColumn = cursor.getColumnIndexOrThrow(AudioColumns.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(AudioColumns.DURATION)
            val isFavoriteColumn = cursor.getColumnIndexOrThrow(AudioColumns.IS_FAVORITE)
            val relativePathColumn = cursor.getColumnIndexOrThrow(AudioColumns.RELATIVE_PATH)
            val sizeColumn = cursor.getColumnIndexOrThrow(AudioColumns.SIZE)
            val titleColumn = cursor.getColumnIndexOrThrow(AudioColumns.TITLE)
            val yearColumn = cursor.getColumnIndexOrThrow(AudioColumns.YEAR)
            val albumIdColumn = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM_ID)
            val artistIdColumn = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST_ID)
            val bookmarkColumn = cursor.getColumnIndexOrThrow(AudioColumns.BOOKMARK)
            val isAlarmColumn = cursor.getColumnIndexOrThrow(AudioColumns.IS_ALARM)
            val isAudiobookColumn = if (BuildVersion.isQ()) cursor.getColumnIndex(AudioColumns.IS_AUDIOBOOK) else -1
            val isMusicColumn = cursor.getColumnIndexOrThrow(AudioColumns.IS_MUSIC)
            val isNotificationColumn = cursor.getColumnIndexOrThrow(AudioColumns.IS_NOTIFICATION)
            val isPodcastColumn = cursor.getColumnIndexOrThrow(AudioColumns.IS_PODCAST)
            val isRecordingColumn = if (BuildVersion.isS()) cursor.getColumnIndex(AudioColumns.IS_RECORDING) else -1
            val isRingtoneColumn = cursor.getColumnIndexOrThrow(AudioColumns.IS_RINGTONE)
            val trackColumn = cursor.getColumnIndexOrThrow(AudioColumns.TRACK)
            val authorColumn = cursor.getColumnIndexOrThrow(AudioColumns.AUTHOR)
            val compilationColumn = cursor.getColumnIndexOrThrow(AudioColumns.COMPILATION)
            val composerColumn = cursor.getColumnIndexOrThrow(AudioColumns.COMPOSER)
            val writerColumn = cursor.getColumnIndexOrThrow(AudioColumns.WRITER)
            val generationAddedColumn = cursor.getColumnIndexOrThrow(AudioColumns.GENERATION_ADDED)
            val generationModifiedColumn = cursor.getColumnIndexOrThrow(AudioColumns.GENERATION_MODIFIED)

            while (cursor.moveToNext()) {
                result += MediaStoreAudioEntity(
                    id = cursor.getLong(idColumn),
                    album = cursor.getStringOrNull(albumColumn),
                    albumArtist = cursor.getStringOrNull(albumArtistColumn),
                    artist = cursor.getStringOrNull(artistColumn),
                    bitrate = cursor.getInt(bitrateColumn),
                    bucketDisplayName = cursor.getString(bucketDisplayNameColumn),
                    bucketId = cursor.getLong(bucketIdColumn),
                    data = cursor.getStringOrNull(dataColumn),
                    dateAdded = cursor.getLong(dateAddedColumn),
                    dateModified = cursor.getLong(dateModifiedColumn),
                    displayName = cursor.getString(displayNameColumn),
                    duration = cursor.getLong(durationColumn),
                    isFavorite = cursor.getInt(isFavoriteColumn),
                    relativePath = cursor.getString(relativePathColumn),
                    size = cursor.getLong(sizeColumn),
                    title = cursor.getString(titleColumn),
                    year = cursor.getIntOrNull(yearColumn),
                    albumId = cursor.getLong(albumIdColumn),
                    artistId = cursor.getLong(artistIdColumn),
                    bookmark = cursor.getIntOrNull(bookmarkColumn),
                    isAlarm = cursor.getInt(isAlarmColumn),
                    isAudiobook = cursor.getIntOrNull(isAudiobookColumn) ?: 0,
                    isMusic = cursor.getInt(isMusicColumn),
                    isNotification = cursor.getInt(isNotificationColumn),
                    isPodcast = cursor.getInt(isPodcastColumn),
                    isRecording = cursor.getIntOrNull(isRecordingColumn) ?: 0,
                    isRingtone = cursor.getInt(isRingtoneColumn),
                    track = cursor.getIntOrNull(trackColumn),
                    author = cursor.getStringOrNull(authorColumn),
                    compilation = cursor.getStringOrNull(compilationColumn),
                    composer = cursor.getStringOrNull(composerColumn),
                    writer = cursor.getStringOrNull(writerColumn),
                    generationAdded = cursor.getLong(generationAddedColumn),
                    generationModified = cursor.getLong(generationModifiedColumn),
                )
            }

            return result
        } finally {
            cursor.close()
        }
    }

}