package dev.olog.presentation.utils.delegates

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

class WeakReferenceDelegate<out T>(ref: T) {

    private val value = WeakReference<T>(ref)

    operator fun getValue(thisRef: Any, property: KProperty<*>) = value.get()
}

fun <T> weakRef(value: T): WeakReferenceDelegate<T> = WeakReferenceDelegate(value)