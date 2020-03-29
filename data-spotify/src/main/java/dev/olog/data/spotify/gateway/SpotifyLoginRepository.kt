package dev.olog.data.spotify.gateway

import dev.olog.data.shared.retrofit.IoResult
import dev.olog.data.spotify.entity.SpotifyToken
import dev.olog.data.spotify.service.SpotifyLoginService
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import kotlin.concurrent.withLock

private const val INVALID_TOKEN = ""

internal class SpotifyLoginRepository @Inject constructor(
    private val service: SpotifyLoginService
) {

    private val lock = ReentrantLock()
    private var token: String = INVALID_TOKEN

    fun isTokenValid(): Boolean {
        return lock.withLock {
            token.isNotBlank()
        }
    }

    fun acquireToken(): String {
        return lock.withLock {
            val result = runBlocking {
                service.getToken()
            }
            if (result is IoResult.Success<SpotifyToken>) {
                token = result.data.access_token
            }
            token
        }
    }

    fun getToken(): String {
        return lock.withLock {
            token
        }
    }

}