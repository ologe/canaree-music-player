package dev.olog.data.api.deezer.entity

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DeezerAlbumDtoTest {

    @Nested
    inner class BestCover {

        @Test
        fun `should be coverXl`() {
            val model = DeezerAlbumDto.EMPTY.copy(
                cover_xl = "cover_xl",
                cover_big = "cover_big",
                cover_medium = "cover_medium",
                cover_small = "cover_small",
            )


            assertEquals("cover_xl", model.bestCover)
        }

        @Test
        fun `should be coverBig`() {
            val model = DeezerAlbumDto.EMPTY.copy(
                cover_xl = "",
                cover_big = "cover_big",
                cover_medium = "cover_medium",
                cover_small = "cover_small",
            )

            assertEquals("cover_big", model.bestCover)
        }

        @Test
        fun `should be coverMedium`() {
            val model = DeezerAlbumDto.EMPTY.copy(
                cover_xl = "",
                cover_big = "",
                cover_medium = "cover_medium",
                cover_small = "cover_small",
            )

            assertEquals("cover_medium", model.bestCover)
        }

        @Test
        fun `should be coverSmall`() {
            val model = DeezerAlbumDto.EMPTY.copy(
                cover_xl = "",
                cover_big = "",
                cover_medium = "",
                cover_small = "cover_small",
            )

            assertEquals("cover_small", model.bestCover)
        }

        @Test
        fun `should be null`() {
            val model = DeezerAlbumDto.EMPTY.copy(
                cover_xl = "",
                cover_big = "",
                cover_medium = "",
                cover_small = "",
            )

            assertEquals(null, model.bestCover)
        }

    }
    
}