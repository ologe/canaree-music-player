package dev.olog.core

import dev.olog.project.hackerrank.app.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher

class MockDispatchers(
    private val dispatcher: CoroutineDispatcher
) : Schedulers {

    override val io: CoroutineDispatcher
        get() = dispatcher
    override val cpu: CoroutineDispatcher
        get() = dispatcher
    override val main: CoroutineDispatcher
        get() = dispatcher
}

fun TestCoroutineDispatcher.toMockSchedulers(): Schedulers {
    return MockDispatchers(this)
}