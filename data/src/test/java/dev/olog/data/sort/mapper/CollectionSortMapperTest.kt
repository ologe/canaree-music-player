package dev.olog.data.sort.mapper

import dev.olog.core.entity.sort.CollectionDetailSort
import dev.olog.core.entity.sort.CollectionSort
import dev.olog.data.sort.SortTypeEntity
import org.junit.Assert
import org.junit.Test

class CollectionSortMapperTest {

    @Test
    fun `test all toEntity`() {
        val map = mapOf(
            CollectionSort.Title to SortTypeEntity.Collection,
            CollectionSort.Author to SortTypeEntity.Author,
        )

        for (value in CollectionSort.values()) {
            Assert.assertEquals(map[value], value.toEntity())
        }
    }

    @Test
    fun `test all toCollectionSort`() {
        val map = mapOf(
            SortTypeEntity.Collection to CollectionSort.Title,
            SortTypeEntity.Author to CollectionSort.Author,
        )

        for (item in SortTypeEntity.values()) {
            val actual = try {
                item.toCollectionSort()
            } catch (ex: NoSuchElementException) {
                null
            }
            Assert.assertEquals(map[item], actual)
        }
    }

    @Test
    fun `test detail toEntity`() {
        val map = mapOf(
            CollectionDetailSort.Title to SortTypeEntity.Title,
            CollectionDetailSort.Author to SortTypeEntity.Author,
            CollectionDetailSort.AlbumArtist to SortTypeEntity.AlbumArtist,
            CollectionDetailSort.Duration to SortTypeEntity.Duration,
            CollectionDetailSort.DateAdded to SortTypeEntity.DateAdded,
            CollectionDetailSort.TrackNumber to SortTypeEntity.TrackNumber,
        )

        for (value in CollectionDetailSort.values()) {
            Assert.assertEquals(map[value], value.toEntity())
        }
    }

    @Test
    fun `test detail toDetailCollectionSort`() {
        val map = mapOf(
            SortTypeEntity.Title to CollectionDetailSort.Title,
            SortTypeEntity.Author to CollectionDetailSort.Author,
            SortTypeEntity.AlbumArtist to CollectionDetailSort.AlbumArtist,
            SortTypeEntity.Duration to CollectionDetailSort.Duration,
            SortTypeEntity.DateAdded to CollectionDetailSort.DateAdded,
            SortTypeEntity.TrackNumber to CollectionDetailSort.TrackNumber,
        )

        for (item in SortTypeEntity.values()) {
            val actual = try {
                item.toDetailCollectionSort()
            } catch (ex: NoSuchElementException) {
                null
            }
            Assert.assertEquals(map[item], actual)
        }
    }

}