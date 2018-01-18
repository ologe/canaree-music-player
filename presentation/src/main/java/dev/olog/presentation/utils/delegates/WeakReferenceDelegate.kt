package dev.olog.presentation.utils.delegates

import java.lang.ref.WeakReference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class WeakReferenceDelegate<out T>(ref: T): ReadOnlyProperty<Any, T> {

    private val value = WeakReference<T>(ref)

    override fun getValue(thisRef: Any, property: KProperty<*>): T = value.get()!!

    fun clear(){
        value.clear()
    }

}

fun <T> weakRef(value: T): WeakReferenceDelegate<T> = WeakReferenceDelegate(value)