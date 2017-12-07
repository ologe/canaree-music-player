package dev.olog.shared

class Option<T>(private val item: T?){

    fun isNull() = item == null
    fun isNotNull() = item != null

}
