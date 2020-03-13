package dev.olog.media

import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher

internal fun CoroutineDispatcher.asSchedulers(): Schedulers {
    return object : Schedulers {
        override val io: CoroutineDispatcher
            get() = this@asSchedulers
        override val cpu: CoroutineDispatcher
            get() = this@asSchedulers
        override val main: CoroutineDispatcher
            get() = this@asSchedulers
    }
}