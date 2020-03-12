package dev.olog.core.interactor.lastplayed

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory
import dev.olog.core.MediaIdCategory.ALBUMS
import dev.olog.core.gateway.track.AlbumGateway
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.fail
import org.junit.Test

class InsertLastPlayedAlbumUseCaseTest {

    @Test
    fun testInvokeWithTrack() = runBlockingTest {
        // given
        val id = 1L

        val gateway = mock<AlbumGateway>()

        val sut = InsertLastPlayedAlbumUseCase(gateway)

        // when
        sut(Category(ALBUMS, id))

        // then
        verify(gateway).addLastPlayed(id)
    }


    @Test
    fun testInvokeWithOtherCategories() = runBlockingTest {
        // given
        val id = 1L
        val allowed = listOf(ALBUMS)

        val sut = InsertLastPlayedAlbumUseCase(mock())

        // when
        for (value in MediaIdCategory.values()) {
            if (value in allowed) {
                continue
            }
            try {
                sut(Category(value, id))
                fail("can handle only $allowed, instead was $value")
            } catch (ignored: IllegalArgumentException) {
            }
        }
    }

}