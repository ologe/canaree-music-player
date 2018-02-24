package dev.olog.msc.utils.k.extension

fun Int.negate(): Int {
    if (this >= 0) return this
    return -this
}

fun Long.negate(): Long {
    if (this >= 0) return this
    return -this
}