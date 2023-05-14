package dev.olog.data.mediastore

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Genres
import android.provider.MediaStore.Audio.Playlists
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.core.provider.DocumentsContractCompat
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.data.mediastore.columns.AudioColumns
import dev.olog.platform.BuildVersion
import java.io.File
import javax.inject.Inject

// todo use projection when possible
class MediaStoreQuery @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun queryAllAudio(): List<MediaStoreAudioInternalEntity> {
        val selection = buildString {
            append("${AudioColumns.IS_ALARM} = 0")
            if (BuildVersion.isQ()) {
                append(" AND ${AudioColumns.IS_AUDIOBOOK} = 0")
            }
            append(" AND ${AudioColumns.IS_NOTIFICATION} = 0")
            if (BuildVersion.isS()) {
                append(" AND ${AudioColumns.IS_RECORDING} = 0")
            }
            append(" AND ${AudioColumns.IS_RINGTONE} = 0")
        }

        val cursor = context.contentResolver.query(MediaStoreUris.audio, null, selection, null, null) ?: return emptyList()
        try {
            val result = mutableListOf<MediaStoreAudioInternalEntity>()

            val idColumn = cursor.getColumnIndexOrThrow(AudioColumns._ID)
            val albumColumn = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM)
            val albumArtistColumn = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM_ARTIST)
            val artistColumn = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST)
            val bucketDisplayNameColumn = if (BuildVersion.isQ()) cursor.getColumnIndexOrThrow(AudioColumns.BUCKET_DISPLAY_NAME) else -1
            val bucketIdColumn = if (BuildVersion.isQ()) cursor.getColumnIndexOrThrow(AudioColumns.BUCKET_ID) else -1
            val dataColumn = cursor.getColumnIndexOrThrow(AudioColumns.DATA)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(AudioColumns.DATE_ADDED)
            val displayNameColumn = cursor.getColumnIndexOrThrow(AudioColumns.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(AudioColumns.DURATION)
            val relativePathColumn = if (BuildVersion.isQ()) cursor.getColumnIndexOrThrow(AudioColumns.RELATIVE_PATH) else -1
            val titleColumn = cursor.getColumnIndexOrThrow(AudioColumns.TITLE)
            val yearColumn = cursor.getColumnIndexOrThrow(AudioColumns.YEAR)
            val albumIdColumn = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM_ID)
            val artistIdColumn = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST_ID)
            val isPodcastColumn = cursor.getColumnIndexOrThrow(AudioColumns.IS_PODCAST)
            val trackColumn = cursor.getColumnIndexOrThrow(AudioColumns.TRACK)
            val genreIdColumn = if (BuildVersion.isR()) cursor.getColumnIndexOrThrow(AudioColumns.GENRE_ID) else -1
            val genreColumn = if (BuildVersion.isR()) cursor.getColumnIndexOrThrow(AudioColumns.GENRE) else -1

            while (cursor.moveToNext()) {
                result += MediaStoreAudioInternalEntity(
                    id = cursor.getLong(idColumn),
                    album = cursor.getStringOrNull(albumColumn),
                    albumArtist = cursor.getStringOrNull(albumArtistColumn),
                    artist = cursor.getStringOrNull(artistColumn),
                    bucketDisplayName = getBucketDisplayName(cursor, bucketDisplayNameColumn, dataColumn),
                    bucketId = getBucketId(cursor, bucketIdColumn, dataColumn),
                    data = cursor.getStringOrNull(dataColumn),
                    dateAdded = cursor.getLong(dateAddedColumn),
                    displayName = cursor.getString(displayNameColumn),
                    duration = cursor.getLong(durationColumn),
                    relativePath = getRelativePath(cursor, relativePathColumn, dataColumn),
                    title = cursor.getString(titleColumn),
                    year = cursor.getIntOrNull(yearColumn),
                    albumId = cursor.getLong(albumIdColumn),
                    artistId = cursor.getLong(artistIdColumn),
                    isPodcast = getIsPodcast(cursor, isPodcastColumn),
                    track = cursor.getIntOrNull(trackColumn),
                    genreId = cursor.getLongOrNull(genreIdColumn),
                    genre = cursor.getStringOrNull(genreColumn),
                )
            }

            return result
        } finally {
            cursor.close()
        }
    }

    // TODO is used?
    fun queryAudioById(id: Long): MediaStoreAudioInternalEntity? {
        val selection = buildString {
            append("${AudioColumns.IS_ALARM} = 0")
            if (BuildVersion.isQ()) {
                append(" AND ${AudioColumns.IS_AUDIOBOOK} = 0")
            }
            append(" AND ${AudioColumns.IS_NOTIFICATION} = 0")
            if (BuildVersion.isS()) {
                append(" AND ${AudioColumns.IS_RECORDING} = 0")
            }
            append(" AND ${AudioColumns.IS_RINGTONE} = 0")
            append(" AND ${AudioColumns._ID} = ?")
        }

        val cursor = context.contentResolver.query(
            MediaStoreUris.audio,
            null,
            selection,
            arrayOf(id.toString()),
            null
        ) ?: return null
        try {
            var result: MediaStoreAudioInternalEntity? = null

            val idColumn = cursor.getColumnIndexOrThrow(AudioColumns._ID)
            val albumColumn = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM)
            val albumArtistColumn = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM_ARTIST)
            val artistColumn = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST)
            val bucketDisplayNameColumn = if (BuildVersion.isQ()) cursor.getColumnIndexOrThrow(AudioColumns.BUCKET_DISPLAY_NAME) else -1
            val bucketIdColumn = if (BuildVersion.isQ()) cursor.getColumnIndexOrThrow(AudioColumns.BUCKET_ID) else -1
            val dataColumn = cursor.getColumnIndexOrThrow(AudioColumns.DATA)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(AudioColumns.DATE_ADDED)
            val displayNameColumn = cursor.getColumnIndexOrThrow(AudioColumns.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(AudioColumns.DURATION)
            val relativePathColumn = if (BuildVersion.isQ()) cursor.getColumnIndexOrThrow(AudioColumns.RELATIVE_PATH) else -1
            val titleColumn = cursor.getColumnIndexOrThrow(AudioColumns.TITLE)
            val yearColumn = cursor.getColumnIndexOrThrow(AudioColumns.YEAR)
            val albumIdColumn = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM_ID)
            val artistIdColumn = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST_ID)
            val isPodcastColumn = cursor.getColumnIndexOrThrow(AudioColumns.IS_PODCAST)
            val trackColumn = cursor.getColumnIndexOrThrow(AudioColumns.TRACK)
            val genreIdColumn = if (BuildVersion.isR()) cursor.getColumnIndexOrThrow(AudioColumns.GENRE_ID) else -1
            val genreColumn = if (BuildVersion.isR()) cursor.getColumnIndexOrThrow(AudioColumns.GENRE) else -1

            if (cursor.moveToNext()) {
                result = MediaStoreAudioInternalEntity(
                    id = cursor.getLong(idColumn),
                    album = cursor.getStringOrNull(albumColumn),
                    albumArtist = cursor.getStringOrNull(albumArtistColumn),
                    artist = cursor.getStringOrNull(artistColumn),
                    bucketDisplayName = getBucketDisplayName(cursor, bucketDisplayNameColumn, dataColumn),
                    bucketId = getBucketId(cursor, bucketIdColumn, dataColumn),
                    data = cursor.getStringOrNull(dataColumn),
                    dateAdded = cursor.getLong(dateAddedColumn),
                    displayName = cursor.getString(displayNameColumn),
                    duration = cursor.getLong(durationColumn),
                    relativePath = getRelativePath(cursor, relativePathColumn, dataColumn),
                    title = cursor.getString(titleColumn),
                    year = cursor.getIntOrNull(yearColumn),
                    albumId = cursor.getLong(albumIdColumn),
                    artistId = cursor.getLong(artistIdColumn),
                    isPodcast = getIsPodcast(cursor, isPodcastColumn),
                    track = cursor.getIntOrNull(trackColumn),
                    genreId = cursor.getLongOrNull(genreIdColumn),
                    genre = cursor.getStringOrNull(genreColumn),
                )
            }

            return result
        } finally {
            cursor.close()
        }
    }

    private fun getBucketDisplayName(
        cursor: Cursor,
        columnIndex: Int,
        dataColumnIndex: Int,
    ): String {
        if (columnIndex == -1) {
            val data = cursor.getStringOrNull(dataColumnIndex) ?: return MediaStore.UNKNOWN_STRING
            return File(data).parentFile?.name ?: MediaStore.UNKNOWN_STRING
        }
        return cursor.getStringOrNull(columnIndex) ?: MediaStore.UNKNOWN_STRING
    }

    private fun getBucketId(
        cursor: Cursor,
        columnIndex: Int,
        dataColumnIndex: Int,
    ): Long {
        if (columnIndex == -1) {
            val data = cursor.getStringOrNull(dataColumnIndex) ?: return -1
            return File(data).parentFile?.path?.hashCode()?.toLong() ?: -1
        }
        return cursor.getLong(columnIndex)
    }

    private fun getRelativePath(
        cursor: Cursor,
        columnIndex: Int,
        dataColumnIndex: Int,
    ): String {
        if (columnIndex == -1) {
            val data = cursor.getStringOrNull(dataColumnIndex) ?: return ""
            return File(data).parent.orEmpty()
                .removePrefix(Environment.getExternalStorageDirectory().path)
                .removePrefix(File.separator) + "/"
        }
        return cursor.getString(columnIndex)
    }

    // normalize is_podcast to 0 (false) and 1 (true) in case true is stored with other
    // non zero value
    private fun getIsPodcast(
        cursor: Cursor,
        columnIndex: Int,
    ): Int {
        val isPodcast = cursor.getIntOrNull(columnIndex) ?: return 0
        if (isPodcast == 0) {
            return 0
        }
        return 1
    }

    // TODO check if works with _ID as well
    fun getDisplayNameByUri(uri: Uri): String? {
        // https://developer.android.com/training/secure-file-sharing/retrieve-info
        // content uri has only two field [_id, _display_name]
        return context.contentResolver.query(
            uri,
            arrayOf(AudioColumns.DISPLAY_NAME),
            null,
            null,
            null,
        )?.use {
            it.moveToFirst()
            it.getStringOrNull(0)
        }
    }

    fun queryAllTrackGenres(): List<TrackGenre> {
        val genres = mutableListOf<Genre>()
        val cursor = context.contentResolver.query(MediaStoreUris.genres, null, null, null, null) ?: return emptyList()
        try {
            val idColumn = cursor.getColumnIndexOrThrow(Genres._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(Genres.NAME)

            while (cursor.moveToNext()) {
                genres += Genre(
                    id = cursor.getLong(idColumn),
                    name = cursor.getStringOrNull(nameColumn) ?: continue
                )
            }
        } finally {
            cursor.close()
        }

        return queryAllTrackGenresInternal(genres)
    }

    private fun queryAllTrackGenresInternal(genres: List<Genre>): List<TrackGenre> {
        val result = mutableListOf<TrackGenre>()

        for (genre in genres) {
            val cursor = context.contentResolver.query(
                MediaStoreUris.genreTracks(genre.id),
                arrayOf(Genres.Members.AUDIO_ID),
                null,
                null,
                null
            ) ?: continue

            try {
                val audioIdColumn = cursor.getColumnIndexOrThrow(Genres.Members.AUDIO_ID)
                while (cursor.moveToNext()) {
                    result += TrackGenre(
                        genre = genre,
                        trackId = cursor.getLongOrNull(audioIdColumn) ?: continue
                    )
                }
            } finally {
                cursor.close()
            }
        }

        return result
    }

    @Suppress("deprecation")
    fun queryAllPlaylistsWithTracks(): Map<Playlist, List<PlaylistTrack>> {
        val playlists = mutableListOf<Playlist>()

        val cursor = context.contentResolver.query(
            MediaStoreUris.playlists,
            arrayOf(Playlists._ID, Playlists.NAME, Playlists.DATA),
            null,
            null,
            null
        ) ?: return emptyMap()
        try {
            val idColumn = cursor.getColumnIndexOrThrow(Playlists._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(Playlists.NAME)
            val dataColumn = cursor.getColumnIndexOrThrow(Playlists.DATA)

            while (cursor.moveToNext()) {
                playlists += Playlist(
                    id = cursor.getLong(idColumn),
                    title = cursor.getStringOrNull(nameColumn) ?: continue,
                    path = cursor.getStringOrNull(dataColumn),
                )
            }
        } finally {
            cursor.close()
        }

        return playlists.associateWith { queryPlaylistsTracks(it.id) }
    }

    @Suppress("deprecation")
    fun queryPlaylist(id: Long): Playlist? {
        return context.contentResolver.query(
            MediaStoreUris.playlists,
            arrayOf(Playlists._ID, Playlists.NAME, Playlists.DATA),
            "${Playlists._ID} = ?",
            arrayOf(id.toString()),
            null,
        )?.use { cursor ->
            if (cursor.moveToNext()) {
                val idColumn = cursor.getColumnIndexOrThrow(Playlists._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(Playlists.NAME)
                val dataColumn = cursor.getColumnIndexOrThrow(Playlists.DATA)
                Playlist(
                    id = cursor.getLong(idColumn),
                    title = cursor.getStringOrNull(nameColumn) ?: return null,
                    path = cursor.getStringOrNull(dataColumn),
                )
            } else {
                null
            }
        }
    }

    @Suppress("deprecation")
    fun queryPlaylistsTracks(playlistId: Long): List<PlaylistTrack> {
        val result = mutableListOf<PlaylistTrack>()

        val cursor = context.contentResolver.query(
            MediaStoreUris.playlistTracks(playlistId),
            arrayOf(
                Playlists.Members._ID,
                Playlists.Members.AUDIO_ID,
                Playlists.Members.PLAYLIST_ID,
                Playlists.Members.PLAY_ORDER,
            ),
            null,
            null,
            null
        ) ?: return emptyList()

        try {
            val idColumn = cursor.getColumnIndexOrThrow(Playlists.Members._ID)
            val audioIdColumn = cursor.getColumnIndexOrThrow(Playlists.Members.AUDIO_ID)
            val playlistIdColumn = cursor.getColumnIndexOrThrow(Playlists.Members.PLAYLIST_ID)
            val playOrderColumn = cursor.getColumnIndexOrThrow(Playlists.Members.PLAY_ORDER)
            while (cursor.moveToNext()) {
                result += PlaylistTrack(
                    id = cursor.getLongOrNull(idColumn) ?: continue,
                    audioId = cursor.getLongOrNull(audioIdColumn) ?: continue,
                    playlistId = cursor.getLongOrNull(playlistIdColumn) ?: continue,
                    playOrder = cursor.getIntOrNull(playOrderColumn) ?: continue,
                )
            }
        } finally {
            cursor.close()
        }

        return result
    }

    @Suppress("deprecation")
    fun queryLastAddedPlaylistByTitle(title: String): MediaStorePlaylistInternalEntity? {
        return context.contentResolver.query(
            MediaStoreUris.playlists,
            arrayOf(Playlists._ID, Playlists.DATA),
            "${Playlists.NAME} = ?",
            arrayOf(title),
            "${Playlists.DATE_ADDED} DESC"
        )?.use {
            it.moveToFirst()
            MediaStorePlaylistInternalEntity(
                id = it.getLong(it.getColumnIndexOrThrow(Playlists._ID)),
                title = title,
                path = it.getStringOrNull(it.getColumnIndexOrThrow(Playlists.DATA))
            )

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getDirectoryPath(documentUri: Uri?): String? {
        if (documentUri == null) {
            return null
        }
        if (!DocumentsContractCompat.isTreeUri(documentUri)) {
            Log.e("MediaStoreQuery", "document must be a directory, $documentUri")
            return null
        }
        val document = DocumentFile.fromTreeUri(context, documentUri) ?: return null

        // DocumentFile do not have any `path` method, an easy way to get is to create
        // some temp child file and query it's path

        // create a temp m3u playlist
        val tempFile = document.createFile("audio/x-mpegurl", "canaree_temp.m3u") ?: return null

        // extract the path from the created playlist
        val path = context.contentResolver.query(
            MediaStoreUris.files,
            arrayOf(MediaStore.Files.FileColumns.DATA),
            "${MediaStore.Files.FileColumns.DISPLAY_NAME} = ?",
            arrayOf(tempFile.name),
            null
        )?.use { c ->
            if (c.moveToFirst()) {
                c.getStringOrNull(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
            } else {
                null
            }
        }

        tempFile.delete()

        // file parent file = directory
        return File(path.orEmpty()).parent
    }

    data class Genre(
        val id: Long,
        val name: String,
    )

    data class TrackGenre(
        val genre: Genre,
        val trackId: Long,
    )

    data class Playlist(
        val id: Long,
        val title: String,
        val path: String?,
    )

    data class PlaylistTrack(
        val id: Long,
        val audioId: Long,
        val playlistId: Long,
        val playOrder: Int,
    )

}