package dev.olog.lib.db

import dev.olog.lib.DatabaseBuilder
import dev.olog.lib.model.db.OfflineLyricsEntity
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class OfflineLyricsDaoTest {

    @get:Rule
    val coroutinesRule = MainCoroutineRule()

    private val db by lazy { DatabaseBuilder.build(coroutinesRule.testDispatcher) }
    private val sut by lazy { db.offlineLyricsDao() }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun testSaveAndObserve() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        val lyrics = OfflineLyricsEntity(id, "lyrics")
        assertTrue("should be empty", sut.observeLyrics(id).first().isEmpty())

        // when
        sut.saveLyrics(lyrics)

        // then
        assertEquals(
            listOf(lyrics),
            sut.observeLyrics(id).first()
        )
    }

}