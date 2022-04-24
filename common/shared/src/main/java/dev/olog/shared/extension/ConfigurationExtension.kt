package dev.olog.shared.extension

import android.content.res.Configuration

@Suppress("NOTHING_TO_INLINE")
fun Configuration.isDarkMode(): Boolean {
    return uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}