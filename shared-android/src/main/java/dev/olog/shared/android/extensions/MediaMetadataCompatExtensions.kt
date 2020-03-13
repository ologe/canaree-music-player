package dev.olog.shared.android.extensions

import android.support.v4.media.MediaMetadataCompat

fun MediaMetadataCompat.getBoolean(key: String): Boolean {
    return getLong(key) != 0L
}

fun MediaMetadataCompat.Builder.putBoolean(
    key: String,
    value: Boolean
): MediaMetadataCompat.Builder {
    putLong(key, if (value) 1 else 0)
    return this
}