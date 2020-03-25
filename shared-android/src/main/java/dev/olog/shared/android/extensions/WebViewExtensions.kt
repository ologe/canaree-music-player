package dev.olog.shared.android.extensions

import android.webkit.WebView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

fun WebView.setDarkMode(enable: Boolean) {
    if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
        val mode = if (enable) {
            WebSettingsCompat.FORCE_DARK_ON
        } else {
            WebSettingsCompat.FORCE_DARK_OFF
        }
        WebSettingsCompat.setForceDark(settings, mode)
    }
}