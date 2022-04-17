package dev.olog.core

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class ServiceScope(private val lifecycle: Lifecycle) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = lifecycle.coroutineScope.coroutineContext
}