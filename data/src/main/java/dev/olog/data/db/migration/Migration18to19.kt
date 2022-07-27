package dev.olog.data.db.migration

import android.provider.MediaStore
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.olog.core.entity.sort.Sort
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.blacklist.BlacklistPreferenceLegacy
import dev.olog.data.sort.db.SORT_DIRECTION_ASC
import dev.olog.data.sort.db.SORT_DIRECTION_DESC
import dev.olog.data.sort.db.SORT_TABLE_SONGS
import dev.olog.data.sort.db.SORT_TYPE_ALBUM
import dev.olog.data.sort.db.SORT_TYPE_ARTIST
import dev.olog.data.sort.db.SORT_TYPE_DATE
import dev.olog.data.sort.db.SORT_TYPE_DURATION
import dev.olog.data.sort.db.SortDirectionEntity
import dev.olog.data.sort.db.SortEntityTable
import dev.olog.data.sort.db.SortTypeEntity

class Migration18to19(
    private val blacklistPreferenceLegacy: BlacklistPreferenceLegacy,
    private val sortPreferences: SortPreferences,
) : Migration(18, 19) {

    override fun migrate(database: SupportSQLiteDatabase) {
        createMediaStoreTables(database)
        createBlacklistTables(database)
        createSortTables(database)
    }

    private fun createMediaStoreTables(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS mediastore_audio(
                    id TEXT NOT NULL,
                    artistId TEXT NOT NULL,
                    albumId TEXT NOT NULL,
                    title TEXT NOT NULL,
                    artist TEXT NOT NULL,
                    albumArtist TEXT NOT NULL,
                    album TEXT NOT NULL,
                    duration INTEGER NOT NULL,
                    dateAdded INTEGER NOT NULL,
                    dateModified INTEGER NOT NULL,
                    directory TEXT NOT NULL,
                    path TEXT NOT NULL,
                    discNumber INTEGER NOT NULL,
                    trackNumber INTEGER NOT NULL,
                    isPodcast INTEGER NOT NULL,
                    displayName TEXT NOT NULL,
                    PRIMARY KEY(id)
                )
            """.trimIndent()
        )

        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_id` ON `mediastore_audio` (`id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_artistId` ON `mediastore_audio` (`artistId`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_albumId` ON `mediastore_audio` (`albumId`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_directory` ON `mediastore_audio` (`directory`)")

        database.execSQL("""
            CREATE VIEW `songs_view` AS SELECT mediastore_audio.*
            FROM mediastore_audio
                LEFT JOIN blacklist ON mediastore_audio.directory = blacklist.directory --remove blacklisted
            WHERE blacklist.directory IS NULL AND isPodcast = false
            ORDER BY lower(title) COLLATE UNICODE ASC
        """.trimIndent())

        database.execSQL("""
            CREATE VIEW `songs_view_sorted` AS SELECT songs_view.*
            FROM songs_view
                LEFT JOIN sort ON TRUE -- join with sort to observe table, keep on TRUE so WHERE clause is working
            WHERE sort.tableName = '$SORT_TABLE_SONGS'
            ORDER BY
            -- artist, then title
            CASE WHEN sort.columnName = '$SORT_TYPE_ARTIST' AND artist = '${MediaStore.UNKNOWN_STRING}' THEN -1 END, -- when unknown move last
            CASE WHEN sort.columnName = '$SORT_TYPE_ARTIST' AND sort.direction = '$SORT_DIRECTION_ASC' THEN lower(artist) COLLATE UNICODE END ASC,
            CASE WHEN sort.columnName = '$SORT_TYPE_ARTIST' AND sort.direction = '$SORT_DIRECTION_DESC' THEN lower(artist) COLLATE UNICODE END DESC,
            -- album, then title
            CASE WHEN sort.columnName = '$SORT_TYPE_ALBUM' AND album = '${MediaStore.UNKNOWN_STRING}' THEN -1 END, -- when unknown move last
            CASE WHEN sort.columnName = '$SORT_TYPE_ALBUM' AND sort.direction = '$SORT_DIRECTION_ASC' THEN lower(album) COLLATE UNICODE END ASC,
            CASE WHEN sort.columnName = '$SORT_TYPE_ALBUM' AND sort.direction = '$SORT_DIRECTION_DESC' THEN lower(album) COLLATE UNICODE END DESC,
            -- duration, then title
            CASE WHEN sort.columnName = '$SORT_TYPE_DURATION' AND sort.direction = '$SORT_DIRECTION_ASC' THEN duration END ASC,
            CASE WHEN sort.columnName = '$SORT_TYPE_DURATION' AND sort.direction = '$SORT_DIRECTION_DESC' THEN duration END DESC,
            -- date added, then title
            CASE WHEN sort.columnName = '$SORT_TYPE_DATE' AND sort.direction = '$SORT_DIRECTION_ASC' THEN dateAdded END DESC,
            CASE WHEN sort.columnName = '$SORT_TYPE_DATE' AND sort.direction = '$SORT_DIRECTION_DESC' THEN dateAdded END ASC,
            
            -- default, and second sort
            -- also, CASE WHEN sort.columnName = 'title'
            CASE WHEN sort.direction = '$SORT_DIRECTION_ASC' THEN lower(title) COLLATE UNICODE END ASC,
            CASE WHEN sort.direction = '$SORT_DIRECTION_DESC' THEN lower(title) COLLATE UNICODE END DESC
        """.trimIndent()
        )

        database.execSQL("""
            CREATE VIEW `artists_view` AS SELECT DISTINCT artistId AS id, artist AS name, count(*) AS songs
            FROM songs_view
            GROUP BY artistId
            ORDER BY lower(name) COLLATE UNICODE ASC
        """.trimIndent())

        database.execSQL("""
            CREATE VIEW `artists_view_sorted` AS SELECT artists_view.*
            FROM artists_view
                LEFT JOIN sort ON TRUE -- join with sort to observe table, keep on TRUE so WHERE clause is working
            WHERE sort.tableName = 'artists'
            ORDER BY
            -- author
            CASE WHEN name = '<unknown>' THEN -1 END, -- when unknown move last
            CASE WHEN sort.direction = 'asc' THEN lower(name) END COLLATE UNICODE ASC,
            CASE WHEN sort.direction = 'desc' THEN lower(name) END COLLATE UNICODE DESC
        """.trimIndent())
    }

    private fun createBlacklistTables(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS blacklist(
                directory TEXT NOT NULL,
                PRIMARY KEY(directory)
            )
        """.trimIndent())
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_blacklist_directory` ON `blacklist` (`directory`)")

        val legacyBlacklist = blacklistPreferenceLegacy.getBlackList()
        if (legacyBlacklist.isNotEmpty()) {
            val blacklistValues = legacyBlacklist
                .joinToString(
                    separator = ",",
                    postfix = ";",
                    transform = { "('$it')" }
                )
            blacklistPreferenceLegacy.delete()

            database.execSQL("""
                INSERT INTO blacklist(directory)
                VALUES $blacklistValues
            """.trimIndent())
        }
    }

    private fun createSortTables(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS sort(
                tableName TEXT NOT NULL,
                columnName TEXT NOT NULL,
                direction TEXT NOT NULL,
                PRIMARY KEY(tableName)
            )
        """.trimIndent())

        database.execSQL("""
            INSERT INTO sort (tableName, columnName, direction)
            VALUES 
                ${sortValues()}
        """.trimIndent())
    }

    private fun sortValues(): String {
        val tracksSort = sortPreferences.getAllTracksSort().toRow(SortEntityTable.Songs)
        val albumsSort = sortPreferences.getAllAlbumsSort().toRow(SortEntityTable.Albums)
        val artistsSort = sortPreferences.getAllArtistsSort().toRow(SortEntityTable.Artists)

        val detailFolderSort = sortPreferences.getDetailFolderSort().toRow(SortEntityTable.FoldersSongs)
        val detailPlaylistSort = sortPreferences.getDetailPlaylistSort().toRow(SortEntityTable.PlaylistsSongs)
        val detailAlbumSort = sortPreferences.getDetailAlbumSort().toRow(SortEntityTable.AlbumsSongs)
        val detailArtistSort = sortPreferences.getDetailArtistSort().toRow(SortEntityTable.ArtistsSongs)
        val detailGenreSort = sortPreferences.getDetailGenreSort().toRow(SortEntityTable.GenresSongs)

        return """
            --all songs
            ('folders', 'title', 'asc'),
            ('playlists', 'title', 'asc'),
            ${tracksSort},
            ${artistsSort},
            ${albumsSort},
            ('genres', 'title', 'asc'),
            --all podcasts
            ('podcast_playlists', 'title', 'asc'),
            ('podcast_episodes', 'title', 'asc'),
            ('podcast_artists', 'author', 'asc'),
            ('podcast_albums', 'collection', 'asc'),
            --songs
            ${detailFolderSort},
            ${detailPlaylistSort},
            ${detailAlbumSort},
            ${detailArtistSort},
            ${detailGenreSort},
            --podcasts
            ('podcast_playlists_episodes', 'custom', 'asc'),
            ('podcast_artists_episodes', 'title', 'asc'),
            ('podcast_albums_episodes', 'title', 'asc')
        """.trimIndent()
    }

    private fun Sort.toRow(tableName: SortEntityTable): String {
        val type = SortTypeEntity(type)
        val direction = SortDirectionEntity(direction)
        return "('${tableName}', '${type}', '${direction}')"
    }

}