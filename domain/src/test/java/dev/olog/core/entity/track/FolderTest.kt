package dev.olog.core.entity.track

import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.FOLDERS
import dev.olog.core.Mocks
import org.junit.Assert.assertEquals
import org.junit.Test

class FolderTest {

    @Test
    fun testGetMediaId() {
        val id = 123L
        val folder = Mocks.folder.copy(id = 123)
        assertEquals(
            Category(FOLDERS, id),
            folder.mediaId
        )
    }


}