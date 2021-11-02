package dev.olog.core

import kotlinx.coroutines.flow.Flow

interface Preference<T> {

    fun get(): T
    fun set(value: T)
    fun observe(): Flow<T>
    fun reset()

}