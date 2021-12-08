package dev.olog.data

import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.sort.SortTable
import dev.olog.data.sort.SortTypeEntity
import org.junit.Assert
import org.junit.Test

class SortQueriesTest {

    private val db = TestDatabase()
    private val queries = db.sortQueries

    @Test
    fun `test initial values (all songs)`() {
        // folders
        Assert.assertEquals(
            Sort(SortTable.Folders, SortTypeEntity.Title, SortDirection.ASCENDING),
            queries.select(SortTable.Folders).executeAsOne()
        )

        // playlists
        Assert.assertEquals(
            Sort(SortTable.Playlists, SortTypeEntity.Title, SortDirection.ASCENDING),
            queries.select(SortTable.Playlists).executeAsOne()
        )

        // songs
        Assert.assertEquals(
            Sort(SortTable.Songs, SortTypeEntity.Title, SortDirection.ASCENDING),
            queries.select(SortTable.Songs).executeAsOne()
        )

        // artists
        Assert.assertEquals(
            Sort(SortTable.Artists, SortTypeEntity.Author, SortDirection.ASCENDING),
            queries.select(SortTable.Artists).executeAsOne()
        )

        // albums
        Assert.assertEquals(
            Sort(SortTable.Albums, SortTypeEntity.Collection, SortDirection.ASCENDING),
            queries.select(SortTable.Albums).executeAsOne()
        )

        // genres
        Assert.assertEquals(
            Sort(SortTable.Genres, SortTypeEntity.Title, SortDirection.ASCENDING),
            queries.select(SortTable.Genres).executeAsOne()
        )
    }

    @Test
    fun `test initial values (all podcasts)`() {
        // playlists
        Assert.assertEquals(
            Sort(SortTable.PodcastPlaylists, SortTypeEntity.Title, SortDirection.ASCENDING),
            queries.select(SortTable.PodcastPlaylists).executeAsOne()
        )

        // episodes
        Assert.assertEquals(
            Sort(SortTable.PodcastEpisodes, SortTypeEntity.Title, SortDirection.ASCENDING),
            queries.select(SortTable.PodcastEpisodes).executeAsOne()
        )

        // authors
        Assert.assertEquals(
            Sort(SortTable.PodcastAuthors, SortTypeEntity.Author, SortDirection.ASCENDING),
            queries.select(SortTable.PodcastAuthors).executeAsOne()
        )

        // collections
        Assert.assertEquals(
            Sort(SortTable.PodcastCollections, SortTypeEntity.Collection, SortDirection.ASCENDING),
            queries.select(SortTable.PodcastCollections).executeAsOne()
        )
    }

    @Test
    fun `test initial values (songs)`() {
        // folders
        Assert.assertEquals(
            Sort(SortTable.FoldersSongs, SortTypeEntity.Title, SortDirection.ASCENDING),
            queries.select(SortTable.FoldersSongs).executeAsOne()
        )

        // playlists
        Assert.assertEquals(
            Sort(SortTable.PlaylistsSongs, SortTypeEntity.Custom, SortDirection.ASCENDING),
            queries.select(SortTable.PlaylistsSongs).executeAsOne()
        )

        // artists
        Assert.assertEquals(
            Sort(SortTable.ArtistsSongs, SortTypeEntity.Title, SortDirection.ASCENDING),
            queries.select(SortTable.ArtistsSongs).executeAsOne()
        )

        // albums
        Assert.assertEquals(
            Sort(SortTable.AlbumsSongs, SortTypeEntity.Title, SortDirection.ASCENDING),
            queries.select(SortTable.AlbumsSongs).executeAsOne()
        )

        // genres
        Assert.assertEquals(
            Sort(SortTable.GenresSongs, SortTypeEntity.Title, SortDirection.ASCENDING),
            queries.select(SortTable.GenresSongs).executeAsOne()
        )

    }

    @Test
    fun `test initial values (podcast)`() {
        // playlists
        Assert.assertEquals(
            Sort(SortTable.PodcastPlaylistsEpisodes, SortTypeEntity.Custom, SortDirection.ASCENDING),
            queries.select(SortTable.PodcastPlaylistsEpisodes).executeAsOne()
        )

        // authors
        Assert.assertEquals(
            Sort(SortTable.PodcastAuthorsEpisodes, SortTypeEntity.Title, SortDirection.ASCENDING),
            queries.select(SortTable.PodcastAuthorsEpisodes).executeAsOne()
        )

        // collections
        Assert.assertEquals(
            Sort(SortTable.PodcastCollectionsEpisodes, SortTypeEntity.Title, SortDirection.ASCENDING),
            queries.select(SortTable.PodcastCollectionsEpisodes).executeAsOne()
        )
    }

}