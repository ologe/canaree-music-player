package dev.olog.core.interactor.lastplayed

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory
import dev.olog.core.MediaIdCategory.ALBUMS
import dev.olog.core.catchIaeOnly
import dev.olog.core.gateway.track.AlbumGateway
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class InsertLastPlayedAlbumUseCaseTest {

    private val gateway = mock<AlbumGateway>()
    private val sut = InsertLastPlayedAlbumUseCase(gateway)

    @Test
    fun testInvoke() = runBlockingTest {
        // given
        val id = 1L
        val category = Category(ALBUMS, id)

        // when
        sut(category)

        // then
        verify(gateway).addLastPlayed(id)
    }


    @Test
    fun testInvokeWithOtherCategories() = runBlockingTest {
        // given
        val id = 1L
        val allowed = listOf(ALBUMS)

        // when

        MediaIdCategory.values().catchIaeOnly(allowed) { value ->
            sut(Category(value, id))
        }
    }

}