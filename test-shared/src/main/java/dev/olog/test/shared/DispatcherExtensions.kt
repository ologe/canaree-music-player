package dev.olog.test.shared

import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher

fun CoroutineDispatcher.asSchedulers(): Schedulers {
    return object : Schedulers {
        override val io: CoroutineDispatcher
            get() = this@asSchedulers
        override val cpu: CoroutineDispatcher
            get() = this@asSchedulers
        override val main: CoroutineDispatcher
            get() = this@asSchedulers
    }
}