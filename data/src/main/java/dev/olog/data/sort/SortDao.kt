package dev.olog.data.sort

import com.squareup.sqldelight.Query
import dev.olog.core.entity.sort.*
import dev.olog.data.sort.mapper.*
import javax.inject.Inject
import javax.inject.Singleton
import dev.olog.data.sort.Sort as EntitySort

// TODO try suspend, or see current performance
@Singleton
internal class SortDao @Inject constructor(
    private val queries: SortQueries
) {

    // region folders

    fun getFoldersSortQuery(): Query<Sort<GenericSort>> {
        return queries.select(SortTable.Folders) { _, type, direction ->
            Sort(
                type = type.toGenericSort(),
                direction = direction
            )
        }
    }

    fun setFoldersSort(sort: Sort<GenericSort>) {
        val entity = EntitySort(
            table_name = SortTable.Folders,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    fun getDetailFoldersSortQuery(): Query<Sort<FolderDetailSort>> {
        return queries.select(SortTable.FoldersSongs) { _, type, direction ->
            Sort(
                type = type.toDetailFolderSort(),
                direction = direction
            )
        }
    }

    fun setDetailFoldersSort(sort: Sort<FolderDetailSort>) {
        val entity = EntitySort(
            table_name = SortTable.FoldersSongs,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    // endregion

    // region playlist

    fun getPlaylistsSortQuery(): Query<Sort<GenericSort>> {
        return queries.select(SortTable.Playlists) { _, type, direction ->
            Sort(
                type = type.toGenericSort(),
                direction = direction
            )
        }
    }

    fun setPlaylistsSort(sort: Sort<GenericSort>) {
        val entity = EntitySort(
            table_name = SortTable.Playlists,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    fun getDetailPlaylistsSortQuery(): Query<Sort<PlaylistDetailSort>> {
        return queries.select(SortTable.PlaylistsSongs) { _, type, direction ->
            Sort(
                type = type.toDetailPlaylistSort(),
                direction = direction
            )
        }
    }

    fun setDetailPlaylistsSort(sort: Sort<PlaylistDetailSort>) {
        val entity = EntitySort(
            table_name = SortTable.PlaylistsSongs,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    // endregion

    // region song

    fun getSongsSort(): Query<Sort<PlayableSort>> {
        return queries.select(SortTable.Songs) { _, type, direction ->
            Sort(
                type = type.toPlayableSortSort(),
                direction = direction,
            )
        }
    }

    fun setSongsSort(sort: Sort<PlayableSort>) {
        val entity = EntitySort(
            table_name = SortTable.Songs,
            column_name = sort.type.toEntity(),
            direction = sort.direction
        )
        queries.replace(entity)
    }

    // endregion

    // region artist

    fun getArtistsSortQuery(): Query<Sort<AuthorSort>> {
        return queries.select(SortTable.Artists) { _, type, direction ->
            Sort(
                type = type.toAuthorSort(),
                direction = direction
            )
        }
    }

    fun setArtistsSort(sort: Sort<AuthorSort>) {
        val entity = EntitySort(
            table_name = SortTable.Artists,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    fun getDetailArtistsSortQuery(): Query<Sort<AuthorDetailSort>> {
        return queries.select(SortTable.ArtistsSongs) { _, type, direction ->
            Sort(
                type = type.toDetailAuthorSort(),
                direction = direction
            )
        }
    }

    fun setDetailArtistsSort(sort: Sort<AuthorDetailSort>) {
        val entity = EntitySort(
            table_name = SortTable.ArtistsSongs,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    // endregion

    // region albums

    fun getAlbumsSortQuery(): Query<Sort<CollectionSort>> {
        return queries.select(SortTable.Albums) { _, type, direction ->
            Sort(
                type = type.toCollectionSort(),
                direction = direction
            )
        }
    }

    fun setAlbumsSort(sort: Sort<CollectionSort>) {
        val entity = EntitySort(
            table_name = SortTable.Albums,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    fun getDetailAlbumsSortQuery(): Query<Sort<CollectionDetailSort>> {
        return queries.select(SortTable.AlbumsSongs) { _, type, direction ->
            Sort(
                type = type.toDetailCollectionSort(),
                direction = direction
            )
        }
    }

    fun setDetailAlbumsSort(sort: Sort<CollectionDetailSort>) {
        val entity = EntitySort(
            table_name = SortTable.AlbumsSongs,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    // endregion

    // region genres

    fun getGenresSortQuery(): Query<Sort<GenericSort>> {
        return queries.select(SortTable.Genres) { _, type, direction ->
            Sort(
                type = type.toGenericSort(),
                direction = direction
            )
        }
    }

    fun setGenresSort(sort: Sort<GenericSort>) {
        val entity = EntitySort(
            table_name = SortTable.Genres,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    fun getDetailGenresSortQuery(): Query<Sort<GenreDetailSort>> {
        return queries.select(SortTable.GenresSongs) { _, type, direction ->
            Sort(
                type = type.toDetailGenreSort(),
                direction = direction
            )
        }
    }

    fun setDetailGenresSort(sort: Sort<GenreDetailSort>) {
        val entity = EntitySort(
            table_name = SortTable.GenresSongs,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    // endregion

    // region podcast playlist

    fun getPodcastPlaylistsSortQuery(): Query<Sort<GenericSort>> {
        return queries.select(SortTable.PodcastPlaylists) { _, type, direction ->
            Sort(
                type = type.toGenericSort(),
                direction = direction
            )
        }
    }

    fun setPodcastPlaylistsSort(sort: Sort<GenericSort>) {
        val entity = EntitySort(
            table_name = SortTable.PodcastPlaylists,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    fun getDetailPodcastPlaylistsSortQuery(): Query<Sort<PlaylistDetailSort>> {
        return queries.select(SortTable.PodcastPlaylistsEpisodes) { _, type, direction ->
            Sort(
                type = type.toDetailPlaylistSort(),
                direction = direction
            )
        }
    }

    fun setDetailPodcastPlaylistsSort(sort: Sort<PlaylistDetailSort>) {
        val entity = EntitySort(
            table_name = SortTable.PodcastPlaylistsEpisodes,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    // endregion

    // region podcast episodes

    fun getPodcastEpisodesSort(): Query<Sort<PlayableSort>> {
        return queries.select(SortTable.PodcastEpisodes) { _, type, direction ->
            Sort(
                type = type.toPlayableSortSort(),
                direction = direction,
            )
        }
    }

    fun setPodcastEpisodesSort(sort: Sort<PlayableSort>) {
        val entity = EntitySort(
            table_name = SortTable.PodcastEpisodes,
            column_name = sort.type.toEntity(),
            direction = sort.direction
        )
        queries.replace(entity)
    }

    // endregion

    // region podcast authors

    fun getPodcastAuthorsSortQuery(): Query<Sort<AuthorSort>> {
        return queries.select(SortTable.PodcastAuthors) { _, type, direction ->
            Sort(
                type = type.toAuthorSort(),
                direction = direction
            )
        }
    }

    fun setPodcastAuthorsSort(sort: Sort<AuthorSort>) {
        val entity = EntitySort(
            table_name = SortTable.PodcastAuthors,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    fun getDetailPodcastAuthorsSortQuery(): Query<Sort<AuthorDetailSort>> {
        return queries.select(SortTable.PodcastAuthorsEpisodes) { _, type, direction ->
            Sort(
                type = type.toDetailAuthorSort(),
                direction = direction
            )
        }
    }

    fun setDetailPodcastAuthorsSort(sort: Sort<AuthorDetailSort>) {
        val entity = EntitySort(
            table_name = SortTable.PodcastAuthorsEpisodes,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    // endregion

    // region podcast collections

    fun getPodcastCollectionsSortQuery(): Query<Sort<CollectionSort>> {
        return queries.select(SortTable.PodcastCollections) { _, type, direction ->
            Sort(
                type = type.toCollectionSort(),
                direction = direction
            )
        }
    }

    fun setPodcastCollectionsSort(sort: Sort<CollectionSort>) {
        val entity = EntitySort(
            table_name = SortTable.PodcastCollections,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    fun getDetailPodcastCollectionsSortQuery(): Query<Sort<CollectionDetailSort>> {
        return queries.select(SortTable.PodcastCollectionsEpisodes) { _, type, direction ->
            Sort(
                type = type.toDetailCollectionSort(),
                direction = direction
            )
        }
    }

    fun setDetailPodcastCollectionsSort(sort: Sort<CollectionDetailSort>) {
        val entity = EntitySort(
            table_name = SortTable.PodcastCollectionsEpisodes,
            column_name = sort.type.toEntity(),
            direction = sort.direction,
        )
        queries.replace(entity)
    }

    // endregion

}