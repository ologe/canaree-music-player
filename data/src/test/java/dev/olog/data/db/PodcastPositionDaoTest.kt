package dev.olog.data.db

import dev.olog.data.DatabaseBuilder
import dev.olog.data.model.db.PodcastPositionEntity
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PodcastPositionDaoTest {

    @get:Rule
    val coroutinesRule = MainCoroutineRule()

    private val db by lazy { DatabaseBuilder.build(coroutinesRule.testDispatcher) }
    private val sut by lazy { db.podcastPositionDao() }

    @After
    @Throws(IOException::class)
    fun teardown() {
        db.close()
    }

    @Test
    fun testGetAndSet() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        val positionEntity = PodcastPositionEntity(id, 15000)
        assertNull("should be ull", sut.getPosition(id))

        // when
        sut.setPosition(positionEntity)

        // then
        assertEquals(
            positionEntity.position,
            sut.getPosition(id)
        )
    }

}