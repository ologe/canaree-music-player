package dev.olog.data.repository.lastfm.local

import dev.olog.domain.entity.EMPTY
import dev.olog.domain.entity.LastFmTrack
import dev.olog.data.local.last.fm.LastFmDao
import dev.olog.data.local.last.fm.ImageRetrieverLocalTrack
import dev.olog.data.local.last.fm.LastFmTrackEntity
import dev.olog.data.mapper.toDomain
import dev.olog.data.mapper.toModel
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.StatelessSutTest
import dev.olog.test.shared.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Rule
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ImageRetrieverLocalTrackTest : StatelessSutTest() {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val dao = mockk<LastFmDao>(relaxUnitFun = true)
    private val sut = ImageRetrieverLocalTrack(
        dao = dao
    )

    @Nested
    inner class MustFetch {

        @Test
        fun `should return true when item not exists`() = coroutineRule {
            coEvery { dao.getTrack(1) } returns null

            // when
            val actual = sut.mustFetch(1)

            // then
            assertThat(actual).isTrue()
        }

        @Test
        fun `should return false when item not exists`() = coroutineRule {
            coEvery { dao.getTrack(1) } returns LastFmTrackEntity.EMPTY

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
            val model = LastFmTrackEntity.EMPTY.copy(id = 1)

            coEvery { dao.getTrack(1) } returns model

            // when
            val actual = sut.getCached(1)

            assertThat(actual).isEqualTo(model.toDomain())
        }

        @Test
        fun `should return null when item not exists`() = coroutineRule {
            coEvery { dao.getTrack(1) } returns null

            // when
            val actual = sut.getCached(1)

            assertThat(actual).isNull()
        }

    }

    @Test
    fun `should cache`() = coroutineRule {
        val model = LastFmTrack.EMPTY

        sut.cache(model)

        coVerify { dao.insertTrack(model.toModel()) }
    }

    @Test
    fun `should delete`() = coroutineRule {
        sut.delete(1)

        coVerify { dao.deleteTrack(1) }
    }

}