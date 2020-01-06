package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.Mocks
import org.junit.Assert.assertEquals
import org.junit.Test

class FolderTest {

    @Test
    fun testTrackGetMediaId() {
        val path = "/storage/emulated/"
        val folder = Mocks.folder.copy(path = path)
        assertEquals(
            MediaId.createCategoryValue(MediaIdCategory.FOLDERS, path),
            folder.getMediaId()
        )
    }


}