package dev.olog.core

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class MutableLazy<T>(val init: () -> T) : ReadWriteProperty<Any?, T> {

    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (value == null) {
            value = init()
        }
        return value!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

fun <T> mutableLazy(initializer: () -> T): ReadWriteProperty<Any?, T> =
    MutableLazy(initializer)