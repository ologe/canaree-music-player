package dev.olog.data.utils

import android.database.MatrixCursor

@Suppress("TestFunctionName")
fun MatrixCursor(vararg values: String): MatrixCursor {
    return MatrixCursor(values)
}

fun MatrixCursor.newRow(vararg values: Pair<String, Any?>) {
    val row = newRow()
    for ((v, k) in values) {
        row.add(v, k)
    }
}