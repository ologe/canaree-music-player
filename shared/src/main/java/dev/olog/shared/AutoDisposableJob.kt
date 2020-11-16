package dev.olog.shared

import kotlinx.coroutines.Job
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun autoDisposeJob(): ReadWriteProperty<Any?, Job?> {
    return AutoDisposableJob()
}

private class AutoDisposableJob : ReadWriteProperty<Any?, Job?> {

    private var job: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Job? {
        return job
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Job?) {
        job = value
    }
}