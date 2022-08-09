package dev.olog.data.db.migration

import android.provider.MediaStore.UNKNOWN_STRING
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.olog.data.sort.db.SORT_DIRECTION_ASC
import dev.olog.data.sort.db.SORT_DIRECTION_DESC
import dev.olog.data.sort.db.SORT_TABLE_ALBUMS
import dev.olog.data.sort.db.SORT_TABLE_ARTISTS
import dev.olog.data.sort.db.SORT_TABLE_FOLDERS
import dev.olog.data.sort.db.SORT_TABLE_GENRES
import dev.olog.data.sort.db.SORT_TABLE_SONGS
import dev.olog.data.sort.db.SORT_TYPE_ALBUM
import dev.olog.data.sort.db.SORT_TYPE_ARTIST
import dev.olog.data.sort.db.SORT_TYPE_DATE
import dev.olog.data.sort.db.SORT_TYPE_DURATION

class Migration18to19 : Migration(18, 19) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // mediastore
        createMediaStoreAudioTable(database)
        createMediaStoreGenreTable(database)

        // views
        createSongsView(database)
        createArtistsView(database)
        createAlbumsView(database)
        createFoldersView(database)
        createGenresView(database)

        createBlacklistTables(database)
        createSortTables(database)
    }

    private fun createMediaStoreAudioTable(database: SupportSQLiteDatabase) {
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
                    directoryName TEXT NOT NULL,
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
    }

    private fun createMediaStoreGenreTable(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `mediastore_genre` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, PRIMARY KEY(`id`))
            """.trimIndent()
        )

        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_genre_id` ON `mediastore_genre` (`id`)")

        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS mediastore_genre_track(
                    genreId TEXT NOT NULL,
                    songId TEXT NOT NULL,
                    PRIMARY KEY(genreId, songId)
                )
            """.trimIndent()
        )

        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_genre_track_genreId` ON `mediastore_genre_track` (`genreId`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_genre_track_songId` ON `mediastore_genre_track` (`songId`)")
    }

    private fun createSongsView(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE VIEW `songs_view` AS SELECT mediastore_audio.*
            FROM mediastore_audio
                LEFT JOIN blacklist ON mediastore_audio.directory = blacklist.directory --remove blacklisted
            WHERE blacklist.directory IS NULL AND isPodcast = false
        """.trimIndent())

        database.execSQL("""
            CREATE VIEW `songs_view_sorted` AS SELECT songs_view.*
            FROM songs_view LEFT JOIN sort ON TRUE
            WHERE sort.tableName = '$SORT_TABLE_SONGS'
            ORDER BY
            -- artist, then title
            CASE WHEN sort.columnName = '$SORT_TYPE_ARTIST' AND artist = '$UNKNOWN_STRING' THEN -1 END,
            CASE WHEN sort.columnName = '$SORT_TYPE_ARTIST' AND sort.direction = '$SORT_DIRECTION_ASC' THEN lower(artist) COLLATE UNICODE END ASC,
            CASE WHEN sort.columnName = '$SORT_TYPE_ARTIST' AND sort.direction = '$SORT_DIRECTION_DESC' THEN lower(artist) COLLATE UNICODE END DESC,
            -- album, then title
            CASE WHEN sort.columnName = '$SORT_TYPE_ALBUM' AND album = '$UNKNOWN_STRING' THEN -1 END,
            CASE WHEN sort.columnName = '$SORT_TYPE_ALBUM' AND sort.direction = '$SORT_DIRECTION_ASC' THEN lower(album) COLLATE UNICODE END ASC,
            CASE WHEN sort.columnName = '$SORT_TYPE_ALBUM' AND sort.direction = '$SORT_DIRECTION_DESC' THEN lower(album) COLLATE UNICODE END DESC,
            -- duration, then title
            CASE WHEN sort.columnName = '$SORT_TYPE_DURATION' AND sort.direction = '$SORT_DIRECTION_ASC' THEN duration END ASC,
            CASE WHEN sort.columnName = '$SORT_TYPE_DURATION' AND sort.direction = '$SORT_DIRECTION_DESC' THEN duration END DESC,
            -- date, then title
            CASE WHEN sort.columnName = '$SORT_TYPE_DATE' AND sort.direction = '$SORT_DIRECTION_ASC' THEN dateAdded END DESC,
            CASE WHEN sort.columnName = '$SORT_TYPE_DATE' AND sort.direction = '$SORT_DIRECTION_DESC' THEN dateAdded END ASC,
            -- default, and second sort
            CASE WHEN sort.direction = '$SORT_DIRECTION_ASC' THEN lower(title) COLLATE UNICODE END ASC,
            CASE WHEN sort.direction = '$SORT_DIRECTION_DESC' THEN lower(title) COLLATE UNICODE END DESC
        """.trimIndent())
    }

    private fun createArtistsView(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE VIEW `artists_view` AS SELECT DISTINCT artistId AS id, artist AS name, count(*) AS songs, MIN(dateAdded) as dateAdded 
            FROM songs_view
            GROUP BY artistId
        """.trimIndent())
        database.execSQL("""
            CREATE VIEW `artists_view_sorted` AS SELECT artists_view.*
            FROM artists_view LEFT JOIN sort ON TRUE
            WHERE sort.tableName = '$SORT_TABLE_ARTISTS'
            ORDER BY
            -- artist
            CASE WHEN sort.columnName = '${SORT_TYPE_ARTIST}' AND name = '$UNKNOWN_STRING' THEN -1 END,
            -- date, then artist
            CASE WHEN sort.columnName = '${SORT_TYPE_DATE}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN dateAdded END ASC,
            CASE WHEN sort.columnName = '${SORT_TYPE_DATE}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN dateAdded END DESC,
            -- default, and second sort
            CASE WHEN sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(name) END COLLATE UNICODE ASC,
            CASE WHEN sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(name) END COLLATE UNICODE DESC
        """.trimIndent())
    }

    private fun createAlbumsView(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE VIEW `albums_view` AS SELECT DISTINCT albumId AS id, artistId, album AS title, artist, albumArtist, count(*) AS songs, MIN(dateAdded) as dateAdded, directory
            FROM songs_view
            GROUP BY albumId
        """.trimIndent())

        database.execSQL("""
            CREATE VIEW `albums_view_sorted` AS SELECT albums_view.*
            FROM albums_view LEFT JOIN sort ON TRUE
            WHERE sort.tableName = '$SORT_TABLE_ALBUMS'
            ORDER BY
            -- title, then artist
            CASE WHEN sort.columnName = '${SORT_TYPE_ALBUM}' AND title = '$UNKNOWN_STRING' THEN -1 END,
            -- artist, then artist
            CASE WHEN sort.columnName = '${SORT_TYPE_ARTIST}' AND artist = '$UNKNOWN_STRING' THEN -1 END,
            CASE WHEN sort.columnName = '${SORT_TYPE_ARTIST}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(artist) END COLLATE UNICODE ASC,
            CASE WHEN sort.columnName = '${SORT_TYPE_ARTIST}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(artist) END COLLATE UNICODE DESC,
            -- date, then artist
            CASE WHEN sort.columnName = '${SORT_TYPE_DATE}' AND sort.direction = '${SORT_DIRECTION_ASC}' THEN dateAdded END ASC,
            CASE WHEN sort.columnName = '${SORT_TYPE_DATE}' AND sort.direction = '${SORT_DIRECTION_DESC}' THEN dateAdded END DESC,
            -- default, and second sort
            CASE WHEN sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(title) END COLLATE UNICODE ASC,
            CASE WHEN sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(title) END COLLATE UNICODE DESC
        """.trimIndent())
    }

    private fun createFoldersView(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE VIEW `folders_view` AS SELECT DISTINCT directory AS path, directoryName AS name, count(*) AS songs, MIN(dateAdded) as dateAdded
            FROM songs_view
            GROUP BY directory
        """.trimIndent())

        database.execSQL("""
            CREATE VIEW `folders_view_sorted` AS SELECT folders_view.*
            FROM folders_view LEFT JOIN sort ON TRUE
            WHERE sort.tableName = '$SORT_TABLE_FOLDERS'
            ORDER BY
            -- title
            CASE WHEN sort.direction = '${SORT_DIRECTION_ASC}' THEN lower(name) END COLLATE UNICODE ASC,
            CASE WHEN sort.direction = '${SORT_DIRECTION_DESC}' THEN lower(name) END COLLATE UNICODE DESC
        """.trimIndent())

        // migrate most played
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS most_played_folder_v2 (
                songId TEXT NOT NULL,
                path TEXT NOT NULL,
                timesPlayed INTEGER NOT NULL,
                PRIMARY KEY (songId, path)
            )
        """.trimIndent())

        database.execSQL("""
            INSERT INTO most_played_folder_v2(songId, path, timesPlayed)
            SELECT songId, folderPath, COUNT(*)
            FROM most_played_folder
            GROUP BY songId, folderPath
        """.trimIndent())

        database.execSQL("DROP TABLE most_played_folder")
    }

    private fun createGenresView(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE VIEW `genres_view` AS SELECT mediastore_genre.*, COUNT(*) AS songs 
            FROM mediastore_genre 
                JOIN mediastore_genre_track ON mediastore_genre.id = mediastore_genre_track.genreId
                JOIN songs_view ON mediastore_genre_track.songId = songs_view.id 
            GROUP BY mediastore_genre.id
        """.trimIndent())

        database.execSQL("""
            CREATE VIEW `genres_view_sorted` AS SELECT genres_view.* 
            FROM genres_view LEFT JOIN sort on TRUE
            WHERE sort.tableName = '$SORT_TABLE_GENRES'
            ORDER BY
            -- title
            CASE WHEN sort.direction = '$SORT_DIRECTION_ASC' THEN lower(name) END COLLATE UNICODE ASC,
            CASE WHEN sort.direction = '$SORT_DIRECTION_DESC' THEN lower(name) END COLLATE UNICODE DESC
        """.trimIndent())

        // migrate most played
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS most_played_genre_v2 (
                songId TEXT NOT NULL,
                genreId TEXT NOT NULL,
                timesPlayed INTEGER NOT NULL,
                PRIMARY KEY (songId, genreId)
            )
        """.trimIndent())

        database.execSQL("""
            INSERT INTO most_played_genre_v2(songId, genreId, timesPlayed)
            SELECT songId, genreId, COUNT(*)
            FROM most_played_genre
            GROUP BY songId, genreId
        """.trimIndent())

        database.execSQL("DROP TABLE most_played_genre")
    }

    private fun createBlacklistTables(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS blacklist(
                directory TEXT NOT NULL,
                PRIMARY KEY(directory)
            )
        """.trimIndent())
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_blacklist_directory` ON `blacklist` (`directory`)")
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
    }

}