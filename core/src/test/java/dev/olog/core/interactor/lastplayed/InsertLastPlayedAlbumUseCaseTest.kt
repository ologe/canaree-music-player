package dev.olog.core.interactor.lastplayed

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.MediaIdCategory.ALBUMS
import dev.olog.core.gateway.track.AlbumGateway
import kotlinx.coroutines.runBlocking
import org.junit.Assert.fail
import org.junit.Test

class InsertLastPlayedAlbumUseCaseTest {

    @Test
    fun testInvokeWithTrack() = runBlocking {
        // given
        val id = 1L

        val gateway = mock<AlbumGateway>()

        val sut = InsertLastPlayedAlbumUseCase(gateway)

        // when
        sut(MediaId.createCategoryValue(ALBUMS, id.toString()))

        // then
        verify(gateway).addLastPlayed(id)
    }


    @Test
    fun testInvokeWithOtherCategories() = runBlocking {
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
                sut(MediaId.createCategoryValue(value, id.toString()))
                fail("can handle only $allowed, instead was $value")
            } catch (ignored: IllegalArgumentException) {
            }
        }
    }

}