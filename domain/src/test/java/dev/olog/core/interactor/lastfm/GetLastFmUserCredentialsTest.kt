package dev.olog.core.interactor.lastfm

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.IEncrypter
import dev.olog.core.entity.UserCredentials
import dev.olog.core.prefs.AppPreferencesGateway
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetLastFmUserCredentialsTest {

    private val gateway = mock<AppPreferencesGateway>()
    private val encrypter = mock<IEncrypter>()
    private val sut = GetLastFmUserCredentials(gateway, encrypter)

    @Test
    fun testInvoke() = runBlockingTest {
        val encryptedUsername = "abc"
        val plainUsername = "user"
        val encryptedPassword = "123"
        val plainPassword = "pwd"
        val user = UserCredentials(encryptedUsername, encryptedPassword)

        whenever(gateway.getLastFmCredentials()).thenReturn(user)
        whenever(encrypter.decrypt(user.username)).thenReturn(plainUsername)
        whenever(encrypter.decrypt(user.password)).thenReturn(plainPassword)

        // when
        val actual = sut()

        // then
        verify(encrypter).decrypt(user.username)
        verify(encrypter).decrypt(user.password)
        verify(gateway).getLastFmCredentials()
        assertEquals(
            UserCredentials(plainUsername, plainPassword),
            actual
        )
    }

}