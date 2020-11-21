package dev.olog.data.api.deezer.entity

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DeezerArtistDtoTest {

    @Nested
    inner class BestPicture {

        @Test
        fun `should be pictureXl`() {
            val model = DeezerArtistDto.EMPTY.copy(
                picture_xl = "picture_xl",
                picture_big = "picture_big",
                picture_medium = "picture_medium",
                picture_small = "picture_small",
            )

            assertEquals("picture_xl", model.bestPicture)
        }

        @Test
        fun `should be pictureBig`() {
            val model = DeezerArtistDto.EMPTY.copy(
                picture_xl = "",
                picture_big = "picture_big",
                picture_medium = "picture_medium",
                picture_small = "picture_small",
            )

            assertEquals("picture_big", model.bestPicture)
        }

        @Test
        fun `should be pictureMedium`() {
            val model = DeezerArtistDto.EMPTY.copy(
                picture_xl = "",
                picture_big = "",
                picture_medium = "picture_medium",
                picture_small = "picture_small",
            )

            assertEquals("picture_medium", model.bestPicture)
        }

        @Test
        fun `should be pictureSmall`() {
            val model = DeezerArtistDto.EMPTY.copy(
                picture_xl = "",
                picture_big = "",
                picture_medium = "",
                picture_small = "picture_small",
            )

            assertEquals("picture_small", model.bestPicture)
        }

        @Test
        fun `should be null`() {
            val model = DeezerArtistDto.EMPTY.copy(
                picture_xl = "",
                picture_big = "",
                picture_medium = "",
                picture_small = "",
            )

            assertEquals(null, model.bestPicture)
        }

    }

}