package dev.olog.image.provider.creator

import org.junit.Assert.assertEquals
import org.junit.Test

class ImagesFolderUtilsTest {

    @Test
    fun testFoldersName(){
        assertEquals("folder", ImagesFolderUtils.FOLDER)
        assertEquals("genre", ImagesFolderUtils.GENRE)
        assertEquals("playlist", ImagesFolderUtils.PLAYLIST)
    }

    @Test
    fun testCreateFileNameSuccess(){
        val itemId = "10"
        val progressive = 1L
        val albumsId = listOf(20L, 21L)

        // when
        val folderName = ImagesFolderUtils.createFileName(itemId, progressive, albumsId)

        // then
        assertEquals(
            "10_1(20_21)",
            folderName
        )
    }

    @Test
    fun testCreateFileNameSuccessNoAlbums(){
        val itemId = "10"
        val progressive = 1L
        val albumsId = listOf<Long>()

        // when
        val folderName = ImagesFolderUtils.createFileName(itemId, progressive, albumsId)

        // then
        assertEquals(
            "10_1()",
            folderName
        )
    }

}