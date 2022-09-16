package dev.olog.data.sort

import dev.olog.core.entity.sort.AlbumSongsSort
import dev.olog.core.entity.sort.AlbumSongsSortType
import dev.olog.core.entity.sort.AlbumSortType
import dev.olog.core.entity.sort.AllAlbumsSort
import dev.olog.core.entity.sort.AllArtistsSort
import dev.olog.core.entity.sort.AllFoldersSort
import dev.olog.core.entity.sort.AllGenresSort
import dev.olog.core.entity.sort.AllPodcastAlbumsSort
import dev.olog.core.entity.sort.AllPodcastArtistsSort
import dev.olog.core.entity.sort.AllPodcastsSort
import dev.olog.core.entity.sort.AllSongsSort
import dev.olog.core.entity.sort.ArtistSongsSort
import dev.olog.core.entity.sort.ArtistSongsSortType
import dev.olog.core.entity.sort.ArtistSortType
import dev.olog.core.entity.sort.FolderSongsSort
import dev.olog.core.entity.sort.FolderSongsSortType
import dev.olog.core.entity.sort.FolderSortType
import dev.olog.core.entity.sort.GenreSongsSort
import dev.olog.core.entity.sort.GenreSongsSortType
import dev.olog.core.entity.sort.GenreSortType
import dev.olog.core.entity.sort.PodcastAlbumEpisodesSort
import dev.olog.core.entity.sort.PodcastAlbumEpisodesSortType
import dev.olog.core.entity.sort.PodcastAlbumSortType
import dev.olog.core.entity.sort.PodcastArtistEpisodesSortType
import dev.olog.core.entity.sort.PodcastArtistEpisodesSort
import dev.olog.core.entity.sort.PodcastArtistSortType
import dev.olog.core.entity.sort.PodcastSortType
import dev.olog.core.entity.sort.SongSortType
import dev.olog.data.sort.db.SortDao
import dev.olog.data.sort.db.SortDirectionEntity
import dev.olog.data.sort.db.SortEntity
import dev.olog.data.sort.db.SortEntityTable
import dev.olog.data.sort.db.SortTypeEntity
import javax.inject.Inject

class SortRepository @Inject constructor(
    private val dao: SortDao,
) {

    fun getAllSongsSort(): AllSongsSort {
        val model = dao.getSort(SortEntityTable.Songs)
        return AllSongsSort(
            type = SongSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setAllSongsSort(model: AllSongsSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.Songs,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

    fun getAllArtistsSort(): AllArtistsSort {
        val model = dao.getSort(SortEntityTable.Artists)
        return AllArtistsSort(
            type = ArtistSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setAllArtistsSort(model: AllArtistsSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.Artists,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

    fun getArtistSongsSort(): ArtistSongsSort {
        val model = dao.getSort(SortEntityTable.ArtistsSongs)
        return ArtistSongsSort(
            type = ArtistSongsSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setArtistSongsSort(model: ArtistSongsSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.ArtistsSongs,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

    fun getAllAlbumsSort(): AllAlbumsSort {
        val model = dao.getSort(SortEntityTable.Albums)
        return AllAlbumsSort(
            type = AlbumSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setAllAlbumsSort(model: AllAlbumsSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.Albums,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

    fun getAlbumSongsSort(): AlbumSongsSort {
        val model = dao.getSort(SortEntityTable.AlbumsSongs)
        return AlbumSongsSort(
            type = AlbumSongsSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setAlbumSongsSort(model: AlbumSongsSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.AlbumsSongs,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

    fun getAllFoldersSort(): AllFoldersSort {
        val model = dao.getSort(SortEntityTable.Folders)
        return AllFoldersSort(
            type = FolderSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setAllFolderSort(model: AllFoldersSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.Folders,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

    fun getFolderSongsSort(): FolderSongsSort {
        val model = dao.getSort(SortEntityTable.FoldersSongs)
        return FolderSongsSort(
            type = FolderSongsSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setFolderSongsSort(model: FolderSongsSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.FoldersSongs,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

    fun getAllGenresSort(): AllGenresSort {
        val model = dao.getSort(SortEntityTable.Genres)
        return AllGenresSort(
            type = GenreSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setAllGenresSort(model: AllGenresSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.Genres,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

    fun getGenreSongsSort(): GenreSongsSort {
        val model = dao.getSort(SortEntityTable.GenresSongs)
        return GenreSongsSort(
            type = GenreSongsSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setGenreSongsSort(model: GenreSongsSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.GenresSongs,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

    fun getAllPodcastsSort(): AllPodcastsSort {
        val model = dao.getSort(SortEntityTable.PodcastEpisodes)
        return AllPodcastsSort(
            type = PodcastSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setAllPodcastsSort(model: AllPodcastsSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.PodcastEpisodes,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

    fun getAllPodcastArtistsSort(): AllPodcastArtistsSort {
        val model = dao.getSort(SortEntityTable.PodcastArtists)
        return AllPodcastArtistsSort(
            type = PodcastArtistSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setAllPodcastArtistsSort(model: AllPodcastArtistsSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.PodcastArtists,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

    fun getPodcastArtistEpisodesSort(): PodcastArtistEpisodesSort {
        val model = dao.getSort(SortEntityTable.PodcastArtistsEpisodes)
        return PodcastArtistEpisodesSort(
            type = PodcastArtistEpisodesSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setPodcastArtistEpisodesSort(model: PodcastArtistEpisodesSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.PodcastArtistsEpisodes,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

    fun getAllPodcastAlbumsSort(): AllPodcastAlbumsSort {
        val model = dao.getSort(SortEntityTable.PodcastAlbums)
        return AllPodcastAlbumsSort(
            type = PodcastAlbumSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setAllPodcastAlbumsSort(model: AllPodcastAlbumsSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.PodcastAlbums,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

    fun getPodcastAlbumEpisodesSort(): PodcastAlbumEpisodesSort {
        val model = dao.getSort(SortEntityTable.PodcastAlbumsEpisodes)
        return PodcastAlbumEpisodesSort(
            type = PodcastAlbumEpisodesSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setPodcastAlbumEpisodesSort(model: PodcastAlbumEpisodesSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.PodcastAlbumsEpisodes,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

}