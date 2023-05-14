package dev.olog.data.db.migration

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore.Audio.Playlists
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.olog.core.entity.track.Playlist
import dev.olog.data.mediastore.MediaStoreUris
import dev.olog.data.utils.ContentValues

class Migration18To19(
    private val contentResolver: ContentResolver,
) : Migration(18, 19) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // mediastore audio internal
        database.execSQL("CREATE TABLE IF NOT EXISTS `mediastore_audio_internal` (`_id` INTEGER NOT NULL, `album_id` INTEGER NOT NULL, `artist_id` INTEGER NOT NULL, `title` TEXT NOT NULL COLLATE LOCALIZED, `album` TEXT COLLATE LOCALIZED, `album_artist` TEXT COLLATE LOCALIZED, `artist` TEXT COLLATE LOCALIZED, `bucket_id` INTEGER NOT NULL, `bucket_display_name` TEXT NOT NULL COLLATE UNICODE, `_data` TEXT, `relative_path` TEXT NOT NULL, `_display_name` TEXT NOT NULL, `is_podcast` INTEGER NOT NULL, `duration` INTEGER NOT NULL, `track` INTEGER, `year` INTEGER, `date_added` INTEGER NOT NULL, `genre_id` INTEGER, `genre` TEXT COLLATE UNICODE, PRIMARY KEY(`_id`))")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_internal__id` ON `mediastore_audio_internal` (`_id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_internal_album_id` ON `mediastore_audio_internal` (`album_id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_internal_artist_id` ON `mediastore_audio_internal` (`artist_id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_internal_bucket_id` ON `mediastore_audio_internal` (`bucket_id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_internal_genre_id` ON `mediastore_audio_internal` (`genre_id`)")

        // mediastore audio view
        database.execSQL("CREATE VIEW `mediastore_audio` AS SELECT _id, album_id, artist_id, title, \n    CASE -- remove album as folder name when behaviour\n        WHEN album = bucket_display_name THEN '<unknown>'\n        ELSE album\n    END AS album,\n    album_artist, artist, \n    bucket_id, bucket_display_name, _data, relative_path, _display_name,\n    is_podcast, duration, track, year, date_added, genre_id, genre\nFROM mediastore_audio_internal LEFT JOIN blacklist \n    ON mediastore_audio_internal.relative_path = blacklist.directory\nWHERE blacklist.directory IS NULL")

        // mediastore folders view
        database.execSQL("CREATE VIEW `mediastore_folders` AS SELECT bucket_id, bucket_display_name, relative_path, count(*) as size\nFROM mediastore_audio\nGROUP BY bucket_id")
        // recreate most_played_folder
        database.execSQL("DROP TABLE IF EXISTS `most_played_folder`")
        database.execSQL("CREATE TABLE IF NOT EXISTS `most_played_folder` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `songId` INTEGER NOT NULL, `folderId` INTEGER NOT NULL)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_most_played_folder_id` ON `most_played_folder` (`id`)")

        // mediastore artist view
        database.execSQL("CREATE VIEW `mediastore_artists` AS SELECT artist_id, artist, album_artist, is_podcast, count(*) as size, MAX(date_added) AS date_added\nFROM mediastore_audio\nWHERE artist <> '<unknown>'\nGROUP BY artist_id")

        // mediastore album view
        database.execSQL("CREATE VIEW `mediastore_albums` AS SELECT album_id, artist_id, album, artist, album_artist, is_podcast, count(*) as size, MAX(date_added) AS date_added \nFROM mediastore_audio\nWHERE album <> '<unknown>' AND album_id <> (\n    -- filter out invalid album that use '0' as name from 'storage/emulated/0' device path\n    SELECT album_id\n    FROM mediastore_audio\n    WHERE album = '0' AND bucket_display_name = '<unknown>'\n)\nGROUP BY album_id")

        // mediastore genre view
        database.execSQL("CREATE VIEW `mediastore_genres` AS SELECT genre_id, genre, count(*) as size\nFROM mediastore_audio\nWHERE genre_id IS NOT NULL AND is_podcast = 0 \nGROUP BY genre_id")

        // mediastore playlist view
        database.execSQL("CREATE TABLE IF NOT EXISTS `mediastore_playlist_internal` (`_id` INTEGER NOT NULL, `name` TEXT NOT NULL COLLATE LOCALIZED, `_data` TEXT, PRIMARY KEY(`_id`))")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_playlist_internal__id` ON `mediastore_playlist_internal` (`_id`)")
        database.execSQL("CREATE TABLE IF NOT EXISTS `mediastore_playlist_members_internal` (`_id` INTEGER NOT NULL, `audio_id` INTEGER NOT NULL, `playlist_id` INTEGER NOT NULL, `play_order` INTEGER NOT NULL, PRIMARY KEY(`_id`))")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_playlist_members_internal__id` ON `mediastore_playlist_members_internal` (`_id`)")
        database.execSQL("CREATE VIEW `mediastore_playlists` AS SELECT playlists._id, playlists.name, playlists._data, count(members._id) as size, CASE WHEN (count(CASE WHEN is_podcast = 0 THEN 1 else null END)) >= (count(CASE WHEN is_podcast <> 0 THEN 1 else null END)) THEN 0 ELSE 1 END AS is_podcast \nFROM mediastore_playlist_internal AS playlists \n    LEFT JOIN mediastore_playlist_members_internal AS members ON playlists._id = members.playlist_id\n    LEFT JOIN mediastore_audio AS audio ON audio._id = members.audio_id\n    JOIN playlist_directory -- playlist_directory must contain only 1 row, so just append path for computation \nWHERE CASE \n    WHEN (playlist_directory.id = -1) THEN 1 -- 1 mean true, no filter here\n    ELSE playlists._data LIKE playlist_directory.path || '%'\nEND\nGROUP BY playlists._id")
        database.execSQL("CREATE TABLE IF NOT EXISTS `playlist_directory` (`id` INTEGER NOT NULL, `documentUri` TEXT, `path` TEXT, PRIMARY KEY(`id`))")

        // blacklist
        database.execSQL("CREATE TABLE IF NOT EXISTS `blacklist` (`directory` TEXT NOT NULL, PRIMARY KEY(`directory`))")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_blacklist_directory` ON `blacklist` (`directory`)")

        // TODO make it fail safe? check against security exception
        migratePlaylists(database)
    }

    // TODO migration will likely break on android 10 (Q) because of scoped storage
    private fun migratePlaylists(database: SupportSQLiteDatabase) {
        // migrate playlists
        val uris = writePlaylistsToMediaStore(
            getPlaylists(database, "SELECT id, name FROM playlist")
        )
        val podcastUris = writePlaylistsToMediaStore(
            getPlaylists(database, "SELECT id, name FROM podcast_playlist")
        )

        // migrate playlist tracks
        for ((playlist, uri) in uris) {
            val tracks = queryPlaylistTracks(
                database = database,
                query = "SELECT idInPlaylist, trackId FROM playlist_tracks WHERE playlistId = ?",
                bindArgs = arrayOf(playlist.id),
                mediaStorePlaylistUri = uri,
            )
            contentResolver.bulkInsert(
                MediaStoreUris.playlistTracks(ContentUris.parseId(uri)),
                tracks.toTypedArray(),
            )
        }

        for ((playlist, uri) in podcastUris) {
            val podcasts = queryPlaylistTracks(
                database = database,
                query = "SELECT idInPlaylist, podcastId FROM podcast_playlist_tracks WHERE playlistId = ?",
                bindArgs = arrayOf(playlist.id),
                mediaStorePlaylistUri = uri,
            )
            contentResolver.bulkInsert(
                MediaStoreUris.playlistTracks(ContentUris.parseId(uri)),
                podcasts.toTypedArray(),
            )
        }

        // drop tables
        database.execSQL("DROP TABLE playlist_tracks")
        database.execSQL("DROP TABLE podcast_playlist_tracks")
        database.execSQL("DROP TABLE playlist")
        database.execSQL("DROP TABLE podcast_playlist")
    }

    private fun getPlaylists(
        database: SupportSQLiteDatabase,
        query: String,
    ): List<Playlist> {
        val cursor = database.query(query)
        val items = mutableListOf<Playlist>()

        while (cursor.moveToNext()) {
            items += Playlist(
                id = cursor.getLong(0),
                title = cursor.getString(1),
                size = 0, // don't care
                isPodcast = false, // don't care
                path = null,
            )
        }

        cursor.close()
        return items
    }

    @Suppress("DEPRECATION")
    private fun writePlaylistsToMediaStore(playlists: List<Playlist>): List<Pair<Playlist, Uri>> {
        return playlists.mapNotNull { playlist ->
            val values = ContentValues(
                Playlists.NAME to playlist.title
            )
            val uri = contentResolver.insert(MediaStoreUris.playlists, values) ?: return@mapNotNull null
            playlist to uri
        }
    }

    @Suppress("DEPRECATION")
    private fun queryPlaylistTracks(
        database: SupportSQLiteDatabase,
        query: String,
        bindArgs: Array<out Any?>,
        mediaStorePlaylistUri: Uri,
    ) : List<ContentValues> {
        val mediaStorePlaylistId = ContentUris.parseId(mediaStorePlaylistUri)
        val cursor = database.query(query, bindArgs)
        val result = mutableListOf<ContentValues>()
        while (cursor.moveToNext()) {
            result += ContentValues(
                Playlists.Members.PLAY_ORDER to cursor.getLong(0),
                Playlists.Members.AUDIO_ID to cursor.getLong(1),
                Playlists.Members.PLAYLIST_ID to mediaStorePlaylistId
            )
        }
        cursor.close()
        return result
    }

}