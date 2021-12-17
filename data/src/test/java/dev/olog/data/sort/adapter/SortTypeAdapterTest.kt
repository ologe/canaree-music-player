package dev.olog.data.sort.adapter

import dev.olog.data.sort.SortTypeEntity
import org.junit.Assert
import org.junit.Test

class SortTypeAdapterTest {

    @Test
    fun `test encode`() {
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
        for (item in SortTypeEntity.values()) {
            Assert.assertEquals(map[item], SortTypeAdapter.encode(item))
        }
    }

    @Test
    fun `test decode`() {
        for (item in SortTypeEntity.values()) {
            Assert.assertEquals(item, SortTypeAdapter.decode(item.serialized))
        }
    }

}