package dev.olog.data.sort.mapper

import dev.olog.core.entity.sort.FolderDetailSort
import dev.olog.data.sort.SortTypeEntity
import org.junit.Assert
import org.junit.Test

class FolderSortMapperTest {

    @Test
    fun `test detail toEntity`() {
        val map = mapOf(
            FolderDetailSort.Title to SortTypeEntity.Title,
            FolderDetailSort.Author to SortTypeEntity.Author,
            FolderDetailSort.Collection to SortTypeEntity.Collection,
            FolderDetailSort.AlbumArtist to SortTypeEntity.AlbumArtist,
            FolderDetailSort.Duration to SortTypeEntity.Duration,
            FolderDetailSort.DateAdded to SortTypeEntity.DateAdded,
            FolderDetailSort.TrackNumber to SortTypeEntity.TrackNumber,
        )

        for (value in FolderDetailSort.values()) {
            Assert.assertEquals(map[value], value.toEntity())
        }
    }

    @Test
    fun `test detail toDetailFolderSort`() {
        val map = mapOf(
            SortTypeEntity.Title to FolderDetailSort.Title,
            SortTypeEntity.Author to FolderDetailSort.Author,
            SortTypeEntity.Collection to FolderDetailSort.Collection,
            SortTypeEntity.AlbumArtist to FolderDetailSort.AlbumArtist,
            SortTypeEntity.Duration to FolderDetailSort.Duration,
            SortTypeEntity.DateAdded to FolderDetailSort.DateAdded,
            SortTypeEntity.TrackNumber to FolderDetailSort.TrackNumber,
        )

        for (item in SortTypeEntity.values()) {
            val actual = try {
                item.toDetailFolderSort()
            } catch (ex: NoSuchElementException) {
                null
            }
            Assert.assertEquals(map[item], actual)
        }
    }

}