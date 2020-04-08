package dev.olog.lib.image.loader.creator

import org.junit.Assert.assertEquals
import org.junit.Test

class ImageNameTest {

    @Test
    fun testGetContainedNoAlbumSuccess() {
        // given
        val groupId = 10L
        val imageProgressive = 2L
        val albumsId = listOf<Long>()
        val path = "${groupId}_${imageProgressive}(${albumsId.joinToString("_")})"

        val imageName = ImageName(path)

        // when
        val albums = imageName.albums

        assertEquals(
            albumsId,
            albums
        )
    }

    @Test
    fun testGetContainedSingleAlbumSuccess() {
        // given
        val groupId = 10L
        val imageProgressive = 2L
        val albumsId = listOf(11L)
        val path = "${groupId}_${imageProgressive}(${albumsId.joinToString("_")})"

        val imageName = ImageName(path)

        // when
        val albums = imageName.albums

        assertEquals(
            albumsId,
            albums
        )
    }

    @Test
    fun testGetContainedMultipleAlbumsSuccess() {
        // given
        val groupId = 10L
        val imageProgressive = 2L
        val albumsId = listOf(11L, 12L, 13L)
        val path = "${groupId}_${imageProgressive}(${albumsId.joinToString("_")})"

        val imageName = ImageName(path)

        // when
        val albums = imageName.albums

        assertEquals(
            albumsId,
            albums
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMalformedAlbumsStart() {
        // given
        val groupId = 10L
        val imageProgressive = 2L
        val albumsId = listOf(11L, 12L, 13L)
        val path = "${groupId}_${imageProgressive}${albumsId.joinToString("_")})"

        ImageName(path)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMalformedAlbumsEnd() {
        // given
        val groupId = 10L
        val imageProgressive = 2L
        val albumsId = listOf(11L, 12L, 13L)
        val path = "${groupId}_${imageProgressive}(${albumsId.joinToString("_")}"

        ImageName(path)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMalformedProgressiveWithNoProgressive() {
        // given
        val groupId = 10L
        val albumsId = listOf(11L, 12L, 13L)
        val path = "${groupId}(${albumsId.joinToString("_")})"

        ImageName(path)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMalformedProgressiveMissingUnderscore() {
        // given
        val groupId = 10L
        val imageProgressive = 2L
        val albumsId = listOf(11L, 12L, 13L)
        val path = "${groupId}${imageProgressive}(${albumsId.joinToString("_")})"

        ImageName(path)
    }

    @Test
    fun testGetProgressiveSuccess() {
        // given
        val groupId = 10L
        val imageProgressive = 2L
        val albumsId = listOf(11L, 12L, 13L)
        val path = "${groupId}_${imageProgressive}(${albumsId.joinToString("_")})"

        val imageName = ImageName(path)

        // when
        val progressive = imageName.progressive

        assertEquals(
            imageProgressive,
            progressive
        )
    }

}