package dev.olog.data.sort

import dev.olog.core.sort.*
import dev.olog.data.TestDatabase
import org.junit.Assert
import org.junit.Test

class SortConstraintsTest {

    private val db = TestDatabase()
    private val queries = db.sortQueries

    @Test
    fun `test constraints are same on db and code`() {
        val map = mapOf(
            // all songs
            SortTable.Folders to GenericSort.values().map { it.serialized },
            SortTable.Playlists to GenericSort.values().map { it.serialized },
            SortTable.Songs to TrackSort.values().map { it.serialized },
            SortTable.Artists to AuthorSort.values().map { it.serialized },
            SortTable.Albums to CollectionSort.values().map { it.serialized },
            SortTable.Genres to GenericSort.values().map { it.serialized },
            // all podcasts
            SortTable.PodcastPlaylists to GenericSort.values().map { it.serialized },
            SortTable.PodcastEpisodes to TrackSort.values().map { it.serialized },
            SortTable.PodcastCollections to CollectionSort.values().map { it.serialized },
            SortTable.PodcastAuthors to AuthorSort.values().map { it.serialized },
            // songs
            SortTable.FoldersSongs to FolderDetailSort.values().map { it.serialized },
            SortTable.PlaylistsSongs to PlaylistDetailSort.values().map { it.serialized },
            SortTable.AlbumsSongs to CollectionDetailSort.values().map { it.serialized },
            SortTable.ArtistsSongs to AuthorDetailSort.values().map { it.serialized },
            SortTable.GenresSongs to GenreDetailSort.values().map { it.serialized },
            // podcasts
            SortTable.PodcastPlaylistsEpisodes to PlaylistDetailSort.values().map { it.serialized },
            SortTable.PodcastCollectionsEpisodes to CollectionDetailSort.values().map { it.serialized },
            SortTable.PodcastAuthorsEpisodes to AuthorDetailSort.values().map { it.serialized },
        )
        Assert.assertEquals(map.keys, SortTable.values().toSet())

        for ((table, columns) in map) {
            for (serializedColumn in columns) {
                val column = SortTypeEntity.values().first { it.serialized == serializedColumn }
                for (direction in SortDirection.values()) {
                    val sort = Sort(table, column, SortDirection.ASCENDING)
                    try {
                        queries.replace(sort)
                    } catch (ex: Throwable) {
                        Assert.fail("FOREIGN KEY constraint failed. Table=$table, Column=$column")
                    }
                }
            }
        }
    }

}