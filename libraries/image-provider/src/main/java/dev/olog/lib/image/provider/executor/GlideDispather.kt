package dev.olog.lib.image.provider.executor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Suppress("FunctionName")
internal fun GlideScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)