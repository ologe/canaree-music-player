package dev.olog.data.prefs

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.data.prefs.BlacklistPreferencesImpl.Companion.BLACKLIST
import dev.olog.domain.prefs.BlacklistPreferences
import org.junit.Assert.assertEquals
import org.junit.Test

class BlacklistPreferencesImplTest {

    private val editor = mock<SharedPreferences.Editor>()
    private val prefs = mock<SharedPreferences> {
        on { edit() } doReturn editor
    }

    private val sut: BlacklistPreferences = BlacklistPreferencesImpl(prefs)

    @Test
    fun testGetBlacklistShouldReturnValue() {
        // given
        val expected = setOf("/storage/emulated/0/folder")
        whenever(prefs.getStringSet(BLACKLIST, setOf())).thenReturn(expected)

        // when
        val actual = sut.getBlackList()

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun testGetBlacklistShouldReturnEmpty() {
        // given
        val expected = setOf<String>()
        whenever(prefs.getStringSet(BLACKLIST, setOf())).thenReturn(expected)

        // when
        val actual = sut.getBlackList()

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun testSetBlacklist() {
        // given
        val newBlacklist = setOf("/storage/emulated/0/folder")

        // when
        sut.setBlackList(newBlacklist)

        // then
        verify(editor).putStringSet(BLACKLIST, newBlacklist)
    }

    @Test
    fun testSetDefaultBlacklist() {

        // when
        sut.setDefault()

        // then
        verify(editor).putStringSet(BLACKLIST, setOf())
    }

}