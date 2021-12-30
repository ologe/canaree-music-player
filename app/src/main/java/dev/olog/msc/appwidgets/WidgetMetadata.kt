package dev.olog.msc.appwidgets

import dev.olog.core.MediaUri

data class WidgetMetadata(
    val uri: MediaUri,
    val title: String,
    val subtitle: String
)