package dev.olog.image.provider.internal

import io.github.resilience4j.kotlin.ratelimiter.RateLimiterConfig
import io.github.resilience4j.kotlin.ratelimiter.executeSuspendFunction
import io.github.resilience4j.ratelimiter.RateLimiter
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NetworkCallLimiter @Inject constructor() {

    // 4 calls per seconds
    private val config = RateLimiterConfig {
        limitRefreshPeriod(Duration.ofSeconds(1))
        limitForPeriod(4)
        // no timeout, all calls should be scheduled if still active
        timeoutDuration(Duration.ofDays(1))
    }
    private val limiter = RateLimiter.of(
        NetworkCallLimiter::class.java.name,
        config,
    )

    suspend fun <T> execute(block: suspend () -> T): T {
        return limiter.executeSuspendFunction(block)
    }

}