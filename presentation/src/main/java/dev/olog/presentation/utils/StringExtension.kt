package dev.olog.presentation.utils

import android.text.Html
import android.text.Spanned
import dev.olog.platform.BuildVersion

fun String.asHtml(): Spanned {
    return if (BuildVersion.isNougat()){
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this)
    }
}