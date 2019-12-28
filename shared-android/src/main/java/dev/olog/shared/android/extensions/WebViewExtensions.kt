package dev.olog.shared.android.extensions

import android.webkit.WebView
import dev.olog.shared.android.utils.isQ

fun WebView.setDarkMode(enable: Boolean) {
    if (isQ()) {
        isForceDarkAllowed = enable
    }
}