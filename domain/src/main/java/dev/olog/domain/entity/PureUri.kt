package dev.olog.domain.entity

data class PureUri(
    val scheme: String,
    val ssp: String,
    val fragment: String?
)