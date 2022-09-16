package dev.olog.data.db.migration

import androidx.sqlite.db.SupportSQLiteDatabase
import dev.olog.core.entity.sort.AlbumSortType
import dev.olog.core.entity.sort.ArtistSortType
import dev.olog.core.entity.sort.SongSortType
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortType
import dev.olog.data.blacklist.BlacklistPreferenceLegacy
import dev.olog.data.sort.SortPreferenceLegacy
import dev.olog.data.sort.db.SortDirectionEntity
import dev.olog.data.sort.db.SortEntityTable
import dev.olog.data.sort.db.SortTypeEntity
import javax.inject.Inject

internal class Migration18to19PopulateTables @Inject constructor(
    private val blacklistPreferenceLegacy: BlacklistPreferenceLegacy,
    private val sortPreferences: SortPreferenceLegacy,
) {

    fun execute(db: SupportSQLiteDatabase) {
        migrateBlacklist(db)
        migrateSort(db)
    }

    private fun migrateBlacklist(db: SupportSQLiteDatabase) {
        val legacyBlacklist = blacklistPreferenceLegacy.getBlackList()
        if (legacyBlacklist.isNotEmpty()) {
            val blacklistValues = legacyBlacklist
                .joinToString(
                    separator = ",",
                    postfix = ";",
                    transform = { "('$it')" }
                )
            blacklistPreferenceLegacy.delete()

            db.execSQL(
                """
                INSERT INTO blacklist(directory)
                VALUES $blacklistValues
            """.trimIndent()
            )
        }
    }

    private fun migrateSort(db: SupportSQLiteDatabase) {
        db.execSQL("""
            INSERT INTO sort (tableName, columnName, direction)
            VALUES 
                ${sortValues()}
        """.trimIndent())

        // todo delete legacy sort values
    }

    private fun sortValues(): String {
        val tracksSort = fixTracksSort(sortPreferences.getAllTracksSort())
        val albumsSort = fixAlbumsSort(sortPreferences.getAllAlbumsSort())
        val artistsSort = fixArtistsSort(sortPreferences.getAllArtistsSort())

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

    private fun fixTracksSort(sort: Sort): String {
        val newType = when (sort.type) {
            SortType.TITLE -> SongSortType.Title
            SortType.ARTIST -> SongSortType.Artist
            SortType.ALBUM -> SongSortType.Album
            SortType.DURATION -> SongSortType.Duration
            SortType.RECENTLY_ADDED -> SongSortType.Date
            // reset any incorrect sorts
            SortType.ALBUM_ARTIST,
            SortType.TRACK_NUMBER,
            SortType.CUSTOM -> SongSortType.Title
        }
        return "('${SortEntityTable.Songs}', '${SortTypeEntity(newType.type)}', '${SortDirectionEntity(sort.direction)}')"
    }

    private fun fixAlbumsSort(sort: Sort): String {
        val newType = when (sort.type) {
            SortType.ALBUM, -> AlbumSortType.Title
            SortType.ARTIST -> AlbumSortType.Artist
            SortType.RECENTLY_ADDED -> AlbumSortType.Date
            // reset any incorrect sorts
            SortType.TITLE,
            SortType.DURATION,
            SortType.ALBUM_ARTIST,
            SortType.TRACK_NUMBER,
            SortType.CUSTOM -> AlbumSortType.Title
        }
        return "('${SortEntityTable.Albums}', '${SortTypeEntity(newType.type)}', '${SortDirectionEntity(sort.direction)}')"
    }

    private fun fixArtistsSort(sort: Sort): String {
        val newType = when (sort.type) {
            SortType.ARTIST -> ArtistSortType.Name
            SortType.RECENTLY_ADDED -> ArtistSortType.Date
            // reset any incorrect sorts
            SortType.TITLE,
            SortType.ALBUM_ARTIST,
            SortType.ALBUM,
            SortType.DURATION,
            SortType.TRACK_NUMBER,
            SortType.CUSTOM -> ArtistSortType.Name
        }
        return "('${SortEntityTable.Artists}', '${SortTypeEntity(newType.type)}', '${SortDirectionEntity(sort.direction)}')"
    }

    // todo ensure there a default
    private fun Sort.toRow(tableName: SortEntityTable): String {
        val type = SortTypeEntity(type)
        val direction = SortDirectionEntity(direction)
        return "('${tableName}', '${type}', '${direction}')"
    }

}