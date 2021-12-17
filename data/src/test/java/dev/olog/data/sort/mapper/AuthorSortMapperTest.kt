package dev.olog.data.sort.mapper

import dev.olog.core.entity.sort.AuthorDetailSort
import dev.olog.core.entity.sort.AuthorSort
import dev.olog.data.sort.SortTypeEntity
import org.junit.Assert
import org.junit.Test

class AuthorSortMapperTest {

    @Test
    fun `test all toEntity`() {
        val map = mapOf(
            AuthorSort.Name to SortTypeEntity.Author,
        )

        for (value in AuthorSort.values()) {
            Assert.assertEquals(map[value], value.toEntity())
        }
    }

    @Test
    fun `test all toAuthorSort`() {
        val map = mapOf(
            SortTypeEntity.Author to AuthorSort.Name,
        )

        for (item in SortTypeEntity.values()) {
            val actual = try {
                item.toAuthorSort()
            } catch (ex: NoSuchElementException) {
                null
            }
            Assert.assertEquals(map[item], actual)
        }
    }

    @Test
    fun `test detail toEntity`() {
        val map = mapOf(
            AuthorDetailSort.Title to SortTypeEntity.Title,
            AuthorDetailSort.Collection to SortTypeEntity.Collection,
            AuthorDetailSort.AlbumArtist to SortTypeEntity.AlbumArtist,
            AuthorDetailSort.Duration to SortTypeEntity.Duration,
            AuthorDetailSort.DateAdded to SortTypeEntity.DateAdded,
            AuthorDetailSort.TrackNumber to SortTypeEntity.TrackNumber,
        )

        for (value in AuthorDetailSort.values()) {
            Assert.assertEquals(map[value], value.toEntity())
        }
    }

    @Test
    fun `test detail toDetailAuthorSort`() {
        val map = mapOf(
            SortTypeEntity.Title to AuthorDetailSort.Title,
            SortTypeEntity.Collection to AuthorDetailSort.Collection,
            SortTypeEntity.AlbumArtist to AuthorDetailSort.AlbumArtist,
            SortTypeEntity.Duration to AuthorDetailSort.Duration,
            SortTypeEntity.DateAdded to AuthorDetailSort.DateAdded,
            SortTypeEntity.TrackNumber to AuthorDetailSort.TrackNumber,
        )

        for (item in SortTypeEntity.values()) {
            val actual = try {
                item.toDetailAuthorSort()
            } catch (ex: NoSuchElementException) {
                null
            }
            Assert.assertEquals(map[item], actual)
        }
    }

}