package dev.olog.shared

fun <T> MutableList<T>.cleanThenAdd(list: List<T>){
    clear()
    addAll(list)
}