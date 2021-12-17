package dev.olog.data.sort

import org.junit.Assert
import org.junit.Test

class SortTypeEntityTest {

    @Test
    fun `test serialized value`() {
        val map = mapOf(
            SortTypeEntity.Title to "title",
            SortTypeEntity.Author to "author",
            SortTypeEntity.Collection to "collection",
            SortTypeEntity.AlbumArtist to "album_artist",
            SortTypeEntity.Duration to "duration",
            SortTypeEntity.DateAdded to "date_added",
            SortTypeEntity.TrackNumber to "track_number",
            SortTypeEntity.Custom to "custom",
        )

        for (value in SortTypeEntity.values()) {
            Assert.assertEquals(map[value], value.serialized)
        }
    }

}