package dev.olog.data.local.last.fm

import dev.olog.domain.DateTimeGenerator
import dev.olog.domain.entity.EMPTY
import dev.olog.domain.entity.LastFmArtist
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.StatelessSutTest
import dev.olog.test.shared.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ImageRetrieverLocalArtistTest : StatelessSutTest() {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val dao = mockk<LastFmDao>(relaxUnitFun = true)
    private val dateTimeGenerator = mockk<DateTimeGenerator>()
    private val sut = ImageRetrieverLocalArtistImpl(
        dao = dao,
        dateTimeGenerator = dateTimeGenerator,
    )

    @Nested
    inner class MustFetch {

        @Test
        fun `mustFetch should return true when item not exists`() = coroutineRule {
            coEvery { dao.getArtist(1) } returns null

            // when
            val actual = sut.mustFetch(1)

            // then
            assertThat(actual).isTrue()
        }

        @Test
        fun `mustFetch should return false when item exists`() = coroutineRule {
            coEvery { dao.getArtist(1) } returns LastFmArtistEntity.EMPTY

            // when
            val actual = sut.mustFetch(1)

            // then
            assertThat(actual).isFalse()
        }

    }

    @Nested
    inner class GetCached {

        @Test
        fun `should return item when item exists`() = coroutineRule {
            val model = LastFmArtistEntity.EMPTY.copy(id = 1)

            coEvery { dao.getArtist(1) } returns model

            // when
            val actual = sut.getCached(1)

            assertThat(actual).isEqualTo(model.toDomain())
        }

        @Test
        fun `should return null when item not exists`() = coroutineRule {
            coEvery { dao.getArtist(1) } returns null

            // when
            val actual = sut.getCached(1)

            assertThat(actual).isNull()
        }

    }


    @Test
    fun `should cache`() = coroutineRule {
        every { dateTimeGenerator.formattedNow() } returns "now"
        val model = LastFmArtist.EMPTY

        sut.cache(model)

        coVerify { dao.insertArtist(model.toModel("now")) }
    }

    @Test
    fun `should delete`() = coroutineRule {
        sut.delete(1)

        coVerify { dao.deleteArtist(1) }
    }

}