package dev.olog.data.spotify.gateway

import dev.olog.lib.network.retrofit.IoResult
import dev.olog.data.spotify.entity.RemoteSpotifyToken
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
            if (result is IoResult.Success<RemoteSpotifyToken>) {
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