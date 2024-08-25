package dev.olog.shared.android.utils

import android.content.res.ColorStateList

fun ColorStateList(
    vararg values: Pair<IntArray, Int>
): ColorStateList {
    val states = Array(values.size) { IntArray(0) }
    val colors = IntArray(values.size)

    for (i in values.indices) {
        states[i] = values[i].first
        colors[i] = values[i].second
    }

    return ColorStateList(states, colors)
}