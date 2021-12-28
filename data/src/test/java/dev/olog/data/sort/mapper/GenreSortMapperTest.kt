package dev.olog.data.sort.mapper

import dev.olog.core.sort.GenreDetailSort
import dev.olog.data.sort.SortTypeEntity
import org.junit.Assert
import org.junit.Test

class GenreSortMapperTest {

    @Test
    fun `test detail toEntity`() {
        val map = mapOf(
            GenreDetailSort.Title to SortTypeEntity.Title,
            GenreDetailSort.Author to SortTypeEntity.Author,
            GenreDetailSort.Collection to SortTypeEntity.Collection,
            GenreDetailSort.AlbumArtist to SortTypeEntity.AlbumArtist,
            GenreDetailSort.Duration to SortTypeEntity.Duration,
            GenreDetailSort.DateAdded to SortTypeEntity.DateAdded,
        )

        for (value in GenreDetailSort.values()) {
            Assert.assertEquals(map[value], value.toEntity())
        }
    }

    @Test
    fun `test detail toDetailGenreSort`() {
        val map = mapOf(
            SortTypeEntity.Title to GenreDetailSort.Title,
            SortTypeEntity.Author to GenreDetailSort.Author,
            SortTypeEntity.Collection to GenreDetailSort.Collection,
            SortTypeEntity.AlbumArtist to GenreDetailSort.AlbumArtist,
            SortTypeEntity.Duration to GenreDetailSort.Duration,
            SortTypeEntity.DateAdded to GenreDetailSort.DateAdded,
        )

        for (item in SortTypeEntity.values()) {
            val actual = try {
                item.toDetailGenreSort()
            } catch (ex: NoSuchElementException) {
                null
            }
            Assert.assertEquals(map[item], actual)
        }
    }
    
}