package dev.olog.data.spotify.dto

internal data class RemoteSpotifyPaging<T>(
    val href: String,
    val items: List<T>,
    val limit: Int,
    val next: Any,
    val offset: Int,
    val previous: Any,
    val total: Int
)