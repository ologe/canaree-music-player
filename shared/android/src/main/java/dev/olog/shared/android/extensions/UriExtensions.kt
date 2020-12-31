package dev.olog.shared.android.extensions

import android.net.Uri
import java.net.URI

fun Uri.toJavaUri(): URI {
    return URI.create(this.toString())
}

fun URI.toAndroidUri(): Uri {
    return Uri.parse(this.toString())
}