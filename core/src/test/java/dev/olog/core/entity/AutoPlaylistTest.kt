package dev.olog.core.entity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoPlaylistTest {

    @Test
    fun `test id is hashcode`() {
        val item = AutoPlaylist.LAST_ADDED

        assertEquals(item.hashCode().toLong(), item.id)
    }

    @Test
    fun `test lastAdded is AutoPlaylist`() {
        val id = AutoPlaylist.LAST_ADDED.id

        assertTrue(AutoPlaylist.isAutoPlaylist(id))
    }

    @Test
    fun `test favorite is AutoPlaylist`() {
        val id = AutoPlaylist.FAVORITE.id

        assertTrue(AutoPlaylist.isAutoPlaylist(id))
    }

    @Test
    fun `test history is AutoPlaylist`() {
        val id = AutoPlaylist.HISTORY.id

        assertTrue(AutoPlaylist.isAutoPlaylist(id))
    }

}