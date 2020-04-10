package dev.olog.feature.about.model

internal data class AboutItem(
    val type: Int,
    val itemType: AboutItemType,
    val title: String,
    val subtitle: String
)

