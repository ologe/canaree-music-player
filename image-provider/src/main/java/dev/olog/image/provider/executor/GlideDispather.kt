package dev.olog.image.provider.executor

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

private val glideDispatcher: CoroutineDispatcher by lazy {
    Executors.newFixedThreadPool(4).asCoroutineDispatcher()
}

@Suppress("FunctionName")
internal fun GlideScope(): CoroutineScope = CoroutineScope(SupervisorJob() + glideDispatcher)