package dev.olog.data.spotify.extensions

import androidx.work.WorkInfo

internal fun List<WorkInfo>.getInt(key: String, default: Int): Int {
    if (isNotEmpty()) {
        return get(0).progress.getInt(key, default)
    }
    return -1
}