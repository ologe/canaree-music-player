package dev.olog.data.index

import android.net.Uri

internal class ContentUri(
    val uri: Uri,
    val notifyForDescendants: Boolean
)