package dev.olog.core.preference

import kotlinx.coroutines.flow.Flow

interface Preference<T> {

    fun get(): T
    fun set(value: T)
    fun observe(): Flow<T>
    fun reset()
    fun delete()
    fun exists(): Boolean

}