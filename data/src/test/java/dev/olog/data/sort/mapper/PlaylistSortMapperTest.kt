package dev.olog.data.sort.mapper

import dev.olog.core.sort.PlaylistDetailSort
import dev.olog.data.sort.SortTypeEntity
import org.junit.Assert
import org.junit.Test

class PlaylistSortMapperTest {

    @Test
    fun `test detail toEntity`() {
        val map = mapOf(
            PlaylistDetailSort.Title to SortTypeEntity.Title,
            PlaylistDetailSort.Author to SortTypeEntity.Author,
            PlaylistDetailSort.Collection to SortTypeEntity.Collection,
            PlaylistDetailSort.AlbumArtist to SortTypeEntity.AlbumArtist,
            PlaylistDetailSort.Duration to SortTypeEntity.Duration,
            PlaylistDetailSort.DateAdded to SortTypeEntity.DateAdded,
            PlaylistDetailSort.Custom to SortTypeEntity.Custom,
        )

        for (value in PlaylistDetailSort.values()) {
            Assert.assertEquals(map[value], value.toEntity())
        }
    }

    @Test
    fun `test detail toDetailPlaylistSort`() {
        val map = mapOf(
            SortTypeEntity.Title to PlaylistDetailSort.Title,
            SortTypeEntity.Author to PlaylistDetailSort.Author,
            SortTypeEntity.Collection to PlaylistDetailSort.Collection,
            SortTypeEntity.AlbumArtist to PlaylistDetailSort.AlbumArtist,
            SortTypeEntity.Duration to PlaylistDetailSort.Duration,
            SortTypeEntity.DateAdded to PlaylistDetailSort.DateAdded,
            SortTypeEntity.Custom to PlaylistDetailSort.Custom,
        )

        for (item in SortTypeEntity.values()) {
            val actual = try {
                item.toDetailPlaylistSort()
            } catch (ex: NoSuchElementException) {
                null
            }
            Assert.assertEquals(map[item], actual)
        }
    }
    
}