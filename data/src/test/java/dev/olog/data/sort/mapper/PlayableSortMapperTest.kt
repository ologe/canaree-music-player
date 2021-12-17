package dev.olog.data.sort.mapper

import dev.olog.core.entity.sort.PlayableSort
import dev.olog.data.sort.SortTypeEntity
import org.junit.Assert
import org.junit.Test

class PlayableSortMapperTest {

    @Test
    fun `test all toEntity`() {
        val map = mapOf(
            PlayableSort.Title to SortTypeEntity.Title,
            PlayableSort.Author to SortTypeEntity.Author,
            PlayableSort.Collection to SortTypeEntity.Collection,
            PlayableSort.Duration to SortTypeEntity.Duration,
            PlayableSort.DateAdded to SortTypeEntity.DateAdded,
        )

        for (value in PlayableSort.values()) {
            Assert.assertEquals(map[value], value.toEntity())
        }
    }

    @Test
    fun `test all toDetailPlaylistSort`() {
        val map = mapOf(
            SortTypeEntity.Title to PlayableSort.Title,
            SortTypeEntity.Author to PlayableSort.Author,
            SortTypeEntity.Collection to PlayableSort.Collection,
            SortTypeEntity.Duration to PlayableSort.Duration,
            SortTypeEntity.DateAdded to PlayableSort.DateAdded,
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