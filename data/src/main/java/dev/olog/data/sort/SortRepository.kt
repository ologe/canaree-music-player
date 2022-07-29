package dev.olog.data.sort

import dev.olog.core.entity.sort.AlbumSongsSort
import dev.olog.core.entity.sort.AlbumSongsSortType
import dev.olog.core.entity.sort.AlbumSortType
import dev.olog.core.entity.sort.AllAlbumsSort
import dev.olog.core.entity.sort.AllArtistsSort
import dev.olog.core.entity.sort.AllSongsSort
import dev.olog.core.entity.sort.ArtistSongsSort
import dev.olog.core.entity.sort.ArtistSongsSortType
import dev.olog.core.entity.sort.ArtistSortType
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

}