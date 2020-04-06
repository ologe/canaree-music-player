package dev.olog.domain.entity.track

import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.FOLDERS
import dev.olog.domain.Mocks
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