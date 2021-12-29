package dev.olog.data.sort.mapper

import dev.olog.core.sort.TrackSort
import dev.olog.data.sort.SortTypeEntity
import org.junit.Assert
import org.junit.Test

class PlayableSortMapperTest {

    @Test
    fun `test all toEntity`() {
        val map = mapOf(
            TrackSort.Title to SortTypeEntity.Title,
            TrackSort.Author to SortTypeEntity.Author,
            TrackSort.Collection to SortTypeEntity.Collection,
            TrackSort.Duration to SortTypeEntity.Duration,
            TrackSort.DateAdded to SortTypeEntity.DateAdded,
        )

        for (value in TrackSort.values()) {
            Assert.assertEquals(map[value], value.toEntity())
        }
    }

    @Test
    fun `test all toDetailPlaylistSort`() {
        val map = mapOf(
            SortTypeEntity.Title to TrackSort.Title,
            SortTypeEntity.Author to TrackSort.Author,
            SortTypeEntity.Collection to TrackSort.Collection,
            SortTypeEntity.Duration to TrackSort.Duration,
            SortTypeEntity.DateAdded to TrackSort.DateAdded,
        )

        for (item in SortTypeEntity.values()) {
            val actual = try {
                item.toPlayableSortSort()
            } catch (ex: NoSuchElementException) {
                null
            }
            Assert.assertEquals(map[item], actual)
        }
    }
    
}