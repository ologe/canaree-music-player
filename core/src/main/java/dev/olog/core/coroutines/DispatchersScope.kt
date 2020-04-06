package dev.olog.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("FunctionName")
fun DispatcherScope(dispatcher: CoroutineDispatcher): ReadOnlyProperty<Any?, CoroutineScope> {
    return DispatchersScopeDelegate(dispatcher)
}

private class DispatchersScopeDelegate(
    private val dispatcher: CoroutineDispatcher
) : ReadOnlyProperty<Any?, CoroutineScope> {

    private val scope by lazy { CoroutineScope(SupervisorJob() + dispatcher) }

    override fun getValue(thisRef: Any?, property: KProperty<*>): CoroutineScope {
        return scope
    }
}