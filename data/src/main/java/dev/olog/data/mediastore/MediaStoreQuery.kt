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
import dev.olog.data.mediastore.song.genre.MediaStoreGenreEntity
import dev.olog.data.mediastore.song.genre.MediaStoreGenreTrackEntity
import dev.olog.data.mediastore.song.playlist.MediaStorePlaylistEntity
import dev.olog.data.mediastore.song.playlist.MediaStorePlaylistTrackEntity
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
        val genreUri: Uri = when {
            isQ() -> MediaStore.Audio.Genres.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else -> MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI
        }
        fun genreTracksUri(id: String): Uri {
            return MediaStore.Audio.Genres.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, id.toLong())
        }
        val playlistUri: Uri = when {
            isQ() -> MediaStore.Audio.Playlists.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else -> MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
        }
        fun playlistTracksUri(id: String): Uri {
            return MediaStore.Audio.Playlists.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, id.toLong())
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
            WHERE (${MediaStore.Audio.AudioColumns.IS_MUSIC} != 0 OR ${MediaStore.Audio.AudioColumns.IS_PODCAST} != 0)
                AND ${MediaStore.Audio.AudioColumns.IS_NOTIFICATION} = 0
        """.trimIndent()
        return contextResolver.querySql(sql).mapToMediaStoreAudio()
    }

    fun queryAllGenres(): List<MediaStoreGenreEntity> {
        val sql = """
            SELECT 
                ${MediaStore.Audio.AudioColumns._ID},
                ${MediaStore.Audio.GenresColumns.NAME}
            FROM $genreUri
        """.trimIndent()
        return contextResolver.querySql(sql).mapToMediaStoreGenre()
    }

    fun queryAllGenreSongs(id: String): List<MediaStoreGenreTrackEntity> {
        val sql = """
            SELECT
                ${MediaStore.Audio.Genres.Members.GENRE_ID},
                ${MediaStore.Audio.Genres.Members.AUDIO_ID}
            FROM ${genreTracksUri(id)}
        """.trimIndent()
        return contextResolver.querySql(sql).mapToMediaStoreGenreTracks()
    }

    fun queryAllPlaylists(): List<MediaStorePlaylistEntity> {
        val sql = """
            SELECT 
                ${MediaStore.Audio.AudioColumns._ID},
                ${MediaStore.Audio.PlaylistsColumns.NAME},
                ${MediaStore.Audio.PlaylistsColumns.DATA}
            FROM $playlistUri
        """.trimIndent()
        return contextResolver.querySql(sql).mapToMediaStorePlaylist()
    }

    fun queryAllPlaylistSongs(id: String): List<MediaStorePlaylistTrackEntity> {
        val sql = """
            SELECT 
                ${MediaStore.Audio.Playlists.Members.PLAYLIST_ID},
                ${MediaStore.Audio.Playlists.Members.AUDIO_ID},
                ${MediaStore.Audio.Playlists.Members.PLAY_ORDER}
            FROM ${playlistTracksUri(id)}
        """.trimIndent()
        return contextResolver.querySql(sql).mapToMediaStorePlaylistTracks()
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
                val directory = if (path.isNotBlank()) {
                    path.substring(0, path.lastIndexOf(File.separator).coerceAtLeast(0))
                } else {
                    ""
                }
                val directoryName = directory.substringAfterLast(
                    delimiter = File.separator,
                    missingDelimiterValue = directory
                )

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
                    directory = directory,
                    directoryName = directoryName,
                    discNumber = if (track >= 1000) track / 1000 else 0,
                    trackNumber = if (track >= 1000) track % 1000 else track,
                    isPodcast = getIntOrNull(isPodcastColumn) != 0,
                    displayName = getStringOrNull(displayNameColumn).orEmpty(),
                )
            }
        }
    }

    private fun Cursor.mapToMediaStoreGenre(): List<MediaStoreGenreEntity> = use {
        val idColumn = getColumnIndex(MediaStore.Audio.AudioColumns._ID)
        val nameColumn = getColumnIndex(MediaStore.Audio.Genres.NAME)

        buildList {
            while (moveToNext()) {
                this += MediaStoreGenreEntity(
                    id = getStringOrNull(idColumn) ?: continue,
                    name = getStringOrNull(nameColumn) ?: MediaStore.UNKNOWN_STRING,
                )
            }
        }
    }

    private fun Cursor.mapToMediaStoreGenreTracks(): List<MediaStoreGenreTrackEntity> = use {
        val genreIdColumn = getColumnIndex(MediaStore.Audio.Genres.Members.GENRE_ID)
        val songIdColumn = getColumnIndex(MediaStore.Audio.Genres.Members.AUDIO_ID)

        buildList {
            while (moveToNext()) {
                this += MediaStoreGenreTrackEntity(
                    genreId = getStringOrNull(genreIdColumn) ?: continue,
                    songId = getStringOrNull(songIdColumn) ?: continue,
                )
            }
        }
    }

    private fun Cursor.mapToMediaStorePlaylist(): List<MediaStorePlaylistEntity> = use {
        val idColumn = getColumnIndex(MediaStore.Audio.AudioColumns._ID)
        val nameColumn = getColumnIndex(MediaStore.Audio.PlaylistsColumns.NAME)
        val pathColumn = getColumnIndex(MediaStore.Audio.PlaylistsColumns.DATA)

        buildList {
            while (moveToNext()) {
                this += MediaStorePlaylistEntity(
                    id = getStringOrNull(idColumn) ?: continue,
                    title = getStringOrNull(nameColumn) ?: MediaStore.UNKNOWN_STRING,
                    path = getStringOrNull(pathColumn).orEmpty(),
                )
            }
        }
    }

    private fun Cursor.mapToMediaStorePlaylistTracks(): List<MediaStorePlaylistTrackEntity> = use {
        val playlistIdColumn = getColumnIndex(MediaStore.Audio.Playlists.Members.PLAYLIST_ID)
        val songIdColumn = getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)
        val playOrderColumn = getColumnIndex(MediaStore.Audio.Playlists.Members.PLAY_ORDER)

        buildList {
            while (moveToNext()) {
                this += MediaStorePlaylistTrackEntity(
                    playlistId = getStringOrNull(playlistIdColumn) ?: continue,
                    songId = getStringOrNull(songIdColumn) ?: continue,
                    playOrder = getIntOrNull(playOrderColumn) ?: continue,
                )
            }
        }
    }

}