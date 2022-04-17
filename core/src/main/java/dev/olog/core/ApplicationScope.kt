package dev.olog.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlin.coroutines.CoroutineContext

class ApplicationScope : CoroutineScope {

    private val delegate = MainScope()

    override val coroutineContext: CoroutineContext
        get() = delegate.coroutineContext

}