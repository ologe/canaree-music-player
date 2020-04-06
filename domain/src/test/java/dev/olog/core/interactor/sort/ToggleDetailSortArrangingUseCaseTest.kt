package dev.olog.core.interactor.sort

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.MediaIdCategory
import dev.olog.core.prefs.SortPreferences
import org.junit.Test

class ToggleDetailSortArrangingUseCaseTest {

    private val gateway = mock<SortPreferences>()
    private val sut = ToggleDetailSortArrangingUseCase(gateway)

    @Test
    fun testInvoke() {
        val category = MediaIdCategory.ARTISTS
        sut(category)
        verify(gateway).toggleDetailSortArranging(category)
    }

}