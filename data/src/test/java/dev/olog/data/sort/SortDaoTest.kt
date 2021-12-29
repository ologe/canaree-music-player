package dev.olog.data.sort

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.sort.Sort
import dev.olog.core.sort.*
import dev.olog.data.TestDatabase
import org.junit.Assert
import org.junit.Test
import dev.olog.data.sort.Sort as EntitySort

class SortDaoTest {

    private val db = TestDatabase()
    private val queries = spy(db.sortQueries) // not mocking because impl is using select(mapper)
    private val dao = SortDao(
        queries = queries,
    )

    // region folder

    @Test
    fun `test getFoldersSortQuery`() {
        val actual = dao.getFoldersSortQuery().executeAsOne()
        val expected = Sort(GenericSort.Title, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.Folders), any<(SortTable, SortTypeEntity, SortDirection) -> GenericSort>())
    }

    @Test
    fun `test setFoldersSort`() {
        dao.setFoldersSort(Sort(GenericSort.Title, SortDirection.ASCENDING))
        verify(queries).replace(EntitySort(SortTable.Folders, SortTypeEntity.Title, SortDirection.ASCENDING))
    }

    @Test
    fun `test getDetailFoldersSortQuery`() {
        val actual = dao.getDetailFoldersSortQuery().executeAsOne()
        val expected = Sort(FolderDetailSort.Title, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.FoldersSongs), any<(SortTable, SortTypeEntity, SortDirection) -> FolderDetailSort>())
    }

    @Test
    fun `test setDetailFoldersSort`() {
        dao.setDetailFoldersSort(Sort(FolderDetailSort.Title, SortDirection.ASCENDING))
        verify(queries).replace(EntitySort(SortTable.FoldersSongs, SortTypeEntity.Title, SortDirection.ASCENDING))
    }

    // endregion

    // region playlist

    @Test
    fun `test getPlaylistsSortQuery`() {
        val actual = dao.getPlaylistsSortQuery().executeAsOne()
        val expected = Sort(GenericSort.Title, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.Playlists), any<(SortTable, SortTypeEntity, SortDirection) -> GenericSort>())
    }

    @Test
    fun `test setPlaylistsSort`() {
        dao.setPlaylistsSort(Sort(GenericSort.Title, SortDirection.ASCENDING))
        verify(queries).replace(EntitySort(SortTable.Playlists, SortTypeEntity.Title, SortDirection.ASCENDING))
    }

    @Test
    fun `test getDetailPlaylistsSortQuery`() {
        val actual = dao.getDetailPlaylistsSortQuery().executeAsOne()
        val expected = Sort(PlaylistDetailSort.Custom, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.PlaylistsSongs), any<(SortTable, SortTypeEntity, SortDirection) -> PlaylistDetailSort>())
    }

    @Test
    fun `test setDetailPlaylistsSort`() {
        dao.setDetailPlaylistsSort(Sort(PlaylistDetailSort.Title, SortDirection.ASCENDING))
        verify(queries).replace(EntitySort(SortTable.PlaylistsSongs, SortTypeEntity.Title, SortDirection.ASCENDING))
    }

    // endregion

    // region song

    @Test
    fun `test getSongsSort`() {
        val actual = dao.getSongsSort().executeAsOne()
        val expected = Sort(TrackSort.Title, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.Songs), any<(SortTable, SortTypeEntity, SortDirection) -> TrackSort>())
    }

    @Test
    fun `test setSongsSort`() {
        dao.setSongsSort(Sort(TrackSort.Title, SortDirection.ASCENDING))
        verify(queries).replace(EntitySort(SortTable.Songs, SortTypeEntity.Title, SortDirection.ASCENDING))
    }

    // endregion

    // region artist

    @Test
    fun `test getArtistsSortQuery`() {
        val actual = dao.getArtistsSortQuery().executeAsOne()
        val expected = Sort(AuthorSort.Name, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.Artists), any<(SortTable, SortTypeEntity, SortDirection) -> AuthorSort>())
    }

    @Test
    fun `test setArtistsSort`() {
        dao.setArtistsSort(Sort(AuthorSort.Name, SortDirection.ASCENDING))
        verify(queries).replace(EntitySort(SortTable.Artists, SortTypeEntity.Author, SortDirection.ASCENDING))
    }

    @Test
    fun `test getDetailArtistsSortQuery`() {
        val actual = dao.getDetailArtistsSortQuery().executeAsOne()
        val expected = Sort(AuthorDetailSort.Title, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.ArtistsSongs), any<(SortTable, SortTypeEntity, SortDirection) -> AuthorDetailSort>())
    }

    @Test
    fun `test setDetailArtistsSort`() {
        dao.setDetailArtistsSort(Sort(AuthorDetailSort.Title, SortDirection.ASCENDING))

        verify(queries).replace(EntitySort(SortTable.ArtistsSongs, SortTypeEntity.Title, SortDirection.ASCENDING))
    }

    // endregion

    // region album

    @Test
    fun `test getAlbumsSortQuery`() {
        val actual = dao.getAlbumsSortQuery().executeAsOne()
        val expected = Sort(CollectionSort.Title, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.Albums), any<(SortTable, SortTypeEntity, SortDirection) -> CollectionSort>())
    }

    @Test
    fun `test setAlbumsSort`() {
        dao.setAlbumsSort(Sort(CollectionSort.Title, SortDirection.ASCENDING))
        verify(queries).replace(EntitySort(SortTable.Albums, SortTypeEntity.Collection, SortDirection.ASCENDING))
    }

    @Test
    fun `test getDetailAlbumsSortQuery`() {
        val actual = dao.getDetailAlbumsSortQuery().executeAsOne()
        val expected = Sort(CollectionDetailSort.Title, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.AlbumsSongs), any<(SortTable, SortTypeEntity, SortDirection) -> CollectionDetailSort>())
    }

    @Test
    fun `test setDetailAlbumsSort`() {
        dao.setDetailAlbumsSort(Sort(CollectionDetailSort.Title, SortDirection.ASCENDING))

        verify(queries).replace(EntitySort(SortTable.AlbumsSongs, SortTypeEntity.Title, SortDirection.ASCENDING))
    }

    // endregion

    // region genre

    @Test
    fun `test getGenresSortQuery`() {
        val actual = dao.getGenresSortQuery().executeAsOne()
        val expected = Sort(GenericSort.Title, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.Genres), any<(SortTable, SortTypeEntity, SortDirection) -> GenericSort>())
    }

    @Test
    fun `test setGenresSort`() {
        dao.setGenresSort(Sort(GenericSort.Title, SortDirection.ASCENDING))
        verify(queries).replace(EntitySort(SortTable.Genres, SortTypeEntity.Title, SortDirection.ASCENDING))
    }

    @Test
    fun `test getDetailGenresSortQuery`() {
        val actual = dao.getDetailGenresSortQuery().executeAsOne()
        val expected = Sort(GenreDetailSort.Title, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.GenresSongs), any<(SortTable, SortTypeEntity, SortDirection) -> GenreDetailSort>())
    }

    @Test
    fun `test setDetailGenresSort`() {
        dao.setDetailGenresSort(Sort(GenreDetailSort.Title, SortDirection.ASCENDING))

        verify(queries).replace(EntitySort(SortTable.GenresSongs, SortTypeEntity.Title, SortDirection.ASCENDING))
    }

    // endregion

    // region podcast playlist

    @Test
    fun `test getPodcastPlaylistsSortQuery`() {
        val actual = dao.getPodcastPlaylistsSortQuery().executeAsOne()
        val expected = Sort(GenericSort.Title, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.PodcastPlaylists), any<(SortTable, SortTypeEntity, SortDirection) -> GenericSort>())
    }

    @Test
    fun `test setPodcastPlaylistsSort`() {
        dao.setPodcastPlaylistsSort(Sort(GenericSort.Title, SortDirection.ASCENDING))
        verify(queries).replace(EntitySort(SortTable.PodcastPlaylists, SortTypeEntity.Title, SortDirection.ASCENDING))
    }

    @Test
    fun `test getDetailPodcastPlaylistsSortQuery`() {
        val actual = dao.getDetailPodcastPlaylistsSortQuery().executeAsOne()
        val expected = Sort(PlaylistDetailSort.Custom, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.PodcastPlaylistsEpisodes), any<(SortTable, SortTypeEntity, SortDirection) -> GenreDetailSort>())
    }

    @Test
    fun `test setDetailPodcastPlaylistsSort`() {
        dao.setDetailPodcastPlaylistsSort(Sort(PlaylistDetailSort.Title, SortDirection.ASCENDING))

        verify(queries).replace(EntitySort(SortTable.PodcastPlaylistsEpisodes, SortTypeEntity.Title, SortDirection.ASCENDING))
    }

    // endregion

    // region podcast episode

    @Test
    fun `test getPodcastEpisodesSort`() {
        val actual = dao.getPodcastEpisodesSort().executeAsOne()
        val expected = Sort(TrackSort.Title, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.PodcastEpisodes), any<(SortTable, SortTypeEntity, SortDirection) -> TrackSort>())
    }

    @Test
    fun `test setPodcastEpisodesSort`() {
        dao.setPodcastEpisodesSort(Sort(TrackSort.Title, SortDirection.ASCENDING))
        verify(queries).replace(EntitySort(SortTable.PodcastEpisodes, SortTypeEntity.Title, SortDirection.ASCENDING))
    }

    // endregion

    // region podcast author

    @Test
    fun `test getPodcastAuthorsSortQuery`() {
        val actual = dao.getPodcastAuthorsSortQuery().executeAsOne()
        val expected = Sort(AuthorSort.Name, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.PodcastAuthors), any<(SortTable, SortTypeEntity, SortDirection) -> AuthorSort>())
    }

    @Test
    fun `test setPodcastAuthorsSort`() {
        dao.setPodcastAuthorsSort(Sort(AuthorSort.Name, SortDirection.ASCENDING))
        verify(queries).replace(EntitySort(SortTable.PodcastAuthors, SortTypeEntity.Author, SortDirection.ASCENDING))
    }

    @Test
    fun `test getDetailPodcastAuthorsSortQuery`() {
        val actual = dao.getDetailPodcastAuthorsSortQuery().executeAsOne()
        val expected = Sort(AuthorDetailSort.Title, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.PodcastAuthorsEpisodes), any<(SortTable, SortTypeEntity, SortDirection) -> AuthorDetailSort>())
    }

    @Test
    fun `test setDetailPodcastAuthorsSort`() {
        dao.setDetailPodcastAuthorsSort(Sort(AuthorDetailSort.Title, SortDirection.ASCENDING))

        verify(queries).replace(EntitySort(SortTable.PodcastAuthorsEpisodes, SortTypeEntity.Title, SortDirection.ASCENDING))
    }

    // endregion

    // region podcast collection

    @Test
    fun `test getPodcastCollectionsSortQuery`() {
        val actual = dao.getPodcastCollectionsSortQuery().executeAsOne()
        val expected = Sort(CollectionSort.Title, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.PodcastCollections), any<(SortTable, SortTypeEntity, SortDirection) -> CollectionSort>())
    }

    @Test
    fun `test setPodcastCollectionsSort`() {
        dao.setPodcastCollectionsSort(Sort(CollectionSort.Title, SortDirection.ASCENDING))
        verify(queries).replace(EntitySort(SortTable.PodcastCollections, SortTypeEntity.Collection, SortDirection.ASCENDING))
    }

    @Test
    fun `test getDetailPodcastCollectionsSortQuery`() {
        val actual = dao.getDetailPodcastCollectionsSortQuery().executeAsOne()
        val expected = Sort(CollectionDetailSort.Title, SortDirection.ASCENDING)
        Assert.assertEquals(expected, actual)

        verify(queries).select(eq(SortTable.PodcastCollectionsEpisodes), any<(SortTable, SortTypeEntity, SortDirection) -> CollectionDetailSort>())
    }

    @Test
    fun `test setDetailPodcastCollectionsSort`() {
        dao.setDetailPodcastCollectionsSort(Sort(CollectionDetailSort.Title, SortDirection.ASCENDING))

        verify(queries).replace(EntitySort(SortTable.PodcastCollectionsEpisodes, SortTypeEntity.Title, SortDirection.ASCENDING))
    }

    // endregion

}