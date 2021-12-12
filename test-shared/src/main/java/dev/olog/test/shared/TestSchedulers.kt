package dev.olog.test.shared

import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Suppress("FunctionName")
fun TestSchedulers(
    io: CoroutineDispatcher = Dispatchers.Unconfined,
    cpu: CoroutineDispatcher = Dispatchers.Unconfined,
    main: CoroutineDispatcher = Dispatchers.Unconfined,
): Schedulers {
    return object : Schedulers {
        override val io: CoroutineDispatcher = io
        override val cpu: CoroutineDispatcher = cpu
        override val main: CoroutineDispatcher = main
    }
}