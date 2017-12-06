package dev.olog.domain.entity

data class Playlist (
        val id: Long,
        val title: String,
        val size: Int = -1,
        val image: String = ""
)