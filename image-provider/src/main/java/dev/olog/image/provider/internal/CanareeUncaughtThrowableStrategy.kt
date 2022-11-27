package dev.olog.image.provider.internal

import com.bumptech.glide.load.engine.executor.GlideExecutor
import kotlinx.coroutines.CancellationException

internal class CanareeUncaughtThrowableStrategy : GlideExecutor.UncaughtThrowableStrategy {
    override fun handle(t: Throwable?) {
        if (t is CancellationException) {
            // ignore cancellations
            return
        }
        t?.printStackTrace()
    }
}