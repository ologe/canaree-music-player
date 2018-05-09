package dev.olog.msc.utils.k.extension

import kotlin.math.absoluteValue

fun Int.asNegative(): Int {
    return -absoluteValue
}

fun Long.asNegative(): Long {
    return -absoluteValue
}