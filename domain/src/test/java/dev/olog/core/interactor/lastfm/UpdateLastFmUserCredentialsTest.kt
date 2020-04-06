package dev.olog.core.interactor.lastfm

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.IEncrypter
import dev.olog.core.entity.UserCredentials
import dev.olog.core.prefs.AppPreferencesGateway
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class UpdateLastFmUserCredentialsTest {

    private val gateway = mock<AppPreferencesGateway>()
    private val encrypter = mock<IEncrypter>()
    private val sut = UpdateLastFmUserCredentials(gateway, encrypter)

    @Test
    fun testInvoke() = runBlockingTest {
        val encryptedUsername = "abc"
        val plainUsername = "user"
        val encryptedPassword = "123"
        val plainPassword = "pwd"

        val user = UserCredentials(plainUsername, plainPassword)

        whenever(encrypter.encrypt(user.username)).thenReturn(encryptedUsername)
        whenever(encrypter.encrypt(user.password)).thenReturn(encryptedPassword)

        // when
        sut(user)

        // then
        verify(encrypter).encrypt(user.username)
        verify(encrypter).encrypt(user.password)
        verify(gateway).setLastFmCredentials(UserCredentials(encryptedUsername, encryptedPassword))
    }

}