package dev.olog.core.entity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoPlaylistTest {

    @Test
    fun testId() {
        val item = AutoPlaylist.LAST_ADDED

        assertEquals(item.hashCode().toLong(), item.id)
    }

    @Test
    fun testIsAutoPlaylist() {
        val id = AutoPlaylist.LAST_ADDED.id

        assertTrue(AutoPlaylist.isAutoPlaylist(id))
    }

}